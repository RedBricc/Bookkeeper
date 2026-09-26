# File and media services on the mini PC

This Compose project adds Filestash and Jellyfin over the existing share without
moving its contents or changing Samba. Runtime data lives in
`/home/deploy/file-media`, outside this repository. Its `compose.yaml` and
`scripts/certificates.sh` are symlinks to the tracked files in this directory.

## Access

- `https://files.vallterra.wiki`: Filestash. Select **Home share** and sign in with
  an existing Samba username/password. The configured backend is the existing
  `home-share` at `/mnt/data/Shared`. Samba continues to enforce access rights.
  The passthrough login asks only for username/password; server, share, port,
  and root path are supplied automatically by an authentication mapping.
  Connection metadata alone does not prefill the standard backend form.
- `https://files.vallterra.wiki/admin`: Filestash administration.
- `https://media.vallterra.wiki` (alias `https://movies.vallterra.wiki`): Jellyfin. Initial administrator: **Brick**.
- Generated initial passwords are in `/home/deploy/file-media/credentials.json`
  (mode 0600). Never commit that file, runtime configuration, or certificate keys.

Jellyfin indexes `/mnt/data/Shared/Brick/Faili/Movies` and
`/mnt/data/Shared/Vera/Movies`. Vera's directory was created empty. Personal
photos/videos elsewhere are not mounted into Jellyfin. Both media mounts are
read-only, and metadata/cache are stored outside the share.

Brick's directory mixes films and TV releases, so it is initially a mixed library.
Its videos are playable, but Jellyfin may classify TV episodes as movies with the
current on-disk layout. Properly separating Movies and Shows later will improve
series metadata. Files have not been renamed or reorganized.

## Isolation and resource limits

- Separate Compose project `file-media`; no existing application is recreated.
- The existing `bookkeeper_web` network provides access from `nginx-proxy`.
- Host bindings are loopback-only: Filestash 18334 and Jellyfin 18096.
- Nginx routes only the two new hostnames in `nginx/conf.d/file-media.conf`.
  It resolves backends dynamically so an unavailable media service cannot prevent
  the shared proxy from starting.
- Jellyfin: 2 CPUs, 3 GiB RAM, 512 processes, UID/GID 1000, no capabilities.
- Filestash: 1.5 CPUs, 1.5 GiB RAM, 256 processes, CPU shares 256 (lower
  priority under contention). Intel VA-API handles video encoding and scaling.
- Container logs rotate at 10 MiB, three files each.
- Jellyfin throttles ahead-of-playback transcoding and deletes old segments.
  Expensive chapter/trickplay generation and real-time filesystem monitoring are
  disabled. Use Jellyfin's scheduled/manual library scan after adding files.
- Images are pinned by digest; upgrades are deliberate.

Filestash requires `APPLICATION_URL=files.vallterra.wiki` without a URL scheme.
Its persisted `general.force_ssl` setting must be `true`, yielding frontend
`origin=https://files.vallterra.wiki`. Verify `/api/config` after changing these
settings; a successful HTML response alone does not validate browser redirects.

To reapply the fixed-share login and HTTPS settings, run
`python3 /home/deploy/Bookkeeper/infrastructure/file-media/configure-filestash.py`.
The script uses the private admin credentials file and does not store users'
Samba passwords. Update that private file if the admin password changes.

## Intel acceleration

Host: Ubuntu 24.04, Intel i5-6500 / HD Graphics 530. The host's existing `i915`
driver exposes `/dev/dri/renderD128`; no driver installation or reboot was needed.
Jellyfin's official image supplies Intel userspace drivers. The container receives
only the render device and supplementary render group 993.

Configured: Intel Quick Sync, device `/dev/dri/renderD128`, hardware encoding,
and hardware decoding for H.264, HEVC 8-bit, MPEG-2, VC-1, and VP8. HEVC 10-bit,
VP9 10-bit, AV1, HDR tone mapping, and Intel low-power encoders are disabled.
Unsupported sources can still direct-play on a compatible client; transcoding
them may require software decoding and be limited by the CPU budget.

Validated using `vainfo` and a synthetic 720p H.264 clip: QSV decode, GPU resize
to 360p, and QSV encode completed successfully. This verifies the hardware path;
it does not guarantee real-time playback for every codec, subtitle, or resolution.

Reference: https://jellyfin.org/docs/general/post-install/transcoding/hardware-acceleration/intel/

### Filestash video acceleration

Filestash is built from the pinned upstream image using `filestash.Dockerfile`.
The derivative adds Debian trixie's `intel-media-va-driver-non-free` and `vainfo`,
runs as the original unprivileged `filestash` user, and receives render group 993
and `/dev/dri/renderD128`. No host driver packages are changed. Its persisted
`features.video.encoder` is `h264_vaapi`; `configure-filestash.py` reapplies it.
Restart Filestash after changing that setting because the streaming handler
selects its encoder at startup.

Build/deploy with `docker compose build filestash` followed by
`docker compose up -d --no-deps filestash` in `/home/deploy/file-media`.
The image base is pinned; rebuilding may pick up newer trixie driver packages.

Verified through Filestash's own HLS endpoint: a roughly five-second 720p sample
produced valid H.264/AAC output in 0.36 seconds. The plugin uses GPU encoding and
scaling; decoding and audio processing can still use CPU. Filestash still copies
the entire original file into its container video cache before streaming, so
large files can retain an initial loading delay. This cache is cleared on restart
and by the plugin's expiry timer. Jellyfin remains preferable for long movies.

## DNS and HTTPS

Both names point to the public origin IPv4. `media` uses DNS-only; `files` remains
Cloudflare-proxied with the existing wildcard Cloudflare Origin certificate.
Browsers see Cloudflare's publicly trusted edge certificate for `files`.
Do not switch `files` to DNS-only without provisioning its own public certificate.
DNS-only is preferred for `media`:
Cloudflare's standard proxy has restrictions on self-hosted video and large-file
delivery. Filestash large transfers are also subject to proxy upload/time limits
when `files` is proxied.

Public TCP 443 must forward to `192.168.0.134:443`. ACME HTTP validation and renewal
for `media` also require public TCP 80 to reach `192.168.0.134:80`. Only the ACME path is served over
HTTP; all other requests redirect to HTTPS.

The Let's Encrypt certificate covers `media.vallterra.wiki` and `movies.vallterra.wiki`. Cloudflare rejected
HTTP validation for `files`, so media renewal deliberately does not depend on it.
Certificate state: `/home/deploy/file-media/letsencrypt`. Certificates are copied
into the existing proxy certificate mount at
`/home/deploy/Bookkeeper/devops/certs/wiki/file-media`, which is ignored by Git.

Initial issuance:

```sh
/home/deploy/file-media/scripts/certificates.sh issue
```

Renewal:

```sh
/home/deploy/file-media/scripts/certificates.sh renew
```

The deploy user's crontab runs renewal daily at 03:17 and 15:17 UTC.
Renewal installs the updated certificate and gracefully reloads Nginx after a
successful configuration test. It does not restart any application container.
The certificate command uses a lock to prevent overlapping runs. Renewal logs
live outside this repository.

## Operation and rollback

```sh
cd /home/deploy/file-media
docker compose config --quiet
docker compose ps
docker compose logs --tail 100
docker compose up -d
```

Before changing proxy configuration, run `docker exec nginx-proxy nginx -t`.
Apply valid changes with `docker exec nginx-proxy nginx -s reload`.

To stop only these services: `docker compose stop` in the runtime directory.
To remove their containers: `docker compose down`. The external proxy network,
existing apps, Samba share, and persistent configuration remain intact. Disable
the two virtual hosts separately if removing the services permanently, then test
and reload Nginx. Remove the dedicated certificate cron entry if retiring them.

Back up the runtime configuration and certificate directory separately from Git;
they contain sensitive data. The shared media still needs its own normal backup.
