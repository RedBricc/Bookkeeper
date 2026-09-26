#!/usr/bin/env bash
set -euo pipefail
umask 077
ROOT=/home/deploy/file-media
TLS=/home/deploy/Bookkeeper/devops/certs/wiki/file-media
IMAGE=certbot/certbot@sha256:f70ad0adbb7e117f0fe42a63c553f28ea451edabc0148757b6efcd9735acaa20
exec 9>"$ROOT/certificates.lock"
flock -n 9 || exit 0

run_certbot() {
    docker run --rm --user 1000:1000 \
        -v "$ROOT/letsencrypt:/etc/letsencrypt" \
        -v "$ROOT/certbot-work:/var/lib/letsencrypt" \
        -v "$ROOT/certbot-logs:/var/log/letsencrypt" \
        -v "$TLS/acme:/webroot" "$IMAGE" "$@"
}

case "${1:-renew}" in
    issue)
        run_certbot certonly --webroot -w /webroot --cert-name file-media \
            -d media.vallterra.wiki -d movies.vallterra.wiki \
            --non-interactive --agree-tos --register-unsafely-without-email \
            --keep-until-expiring --expand
        ;;
    renew)
        test -f "$ROOT/letsencrypt/renewal/file-media.conf" || exit 0
        run_certbot renew --cert-name file-media --non-interactive --quiet
        ;;
    *) echo "Usage: $0 [issue|renew]" >&2; exit 2 ;;
esac

SOURCE="$ROOT/letsencrypt/live/file-media"
if cmp -s "$SOURCE/fullchain.pem" "$TLS/fullchain.pem" && \
   cmp -s "$SOURCE/privkey.pem" "$TLS/privkey.pem"; then
    exit 0
fi
install -m 644 "$SOURCE/fullchain.pem" "$TLS/fullchain.pem.new"
install -m 600 "$SOURCE/privkey.pem" "$TLS/privkey.pem.new"
mv "$TLS/fullchain.pem.new" "$TLS/fullchain.pem"
mv "$TLS/privkey.pem.new" "$TLS/privkey.pem"
docker exec nginx-proxy nginx -t
docker exec nginx-proxy nginx -s reload
