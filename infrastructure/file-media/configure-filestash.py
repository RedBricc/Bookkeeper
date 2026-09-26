#!/usr/bin/env python3
"""Configure the fixed Samba login without storing users' share passwords."""
import json
import urllib.request
from pathlib import Path

BASE = "http://127.0.0.1:18334"
HEADERS = {"Content-Type": "application/json", "Accept": "application/json",
           "Host": "files.vallterra.wiki"}


def request(path, data=None):
    req = urllib.request.Request(
        BASE + path, headers=HEADERS,
        data=None if data is None else json.dumps(data).encode())
    with urllib.request.urlopen(req, timeout=20) as response:
        result = json.load(response)
    if result.get("status") != "ok":
        raise RuntimeError("Filestash configuration request failed")
    return result.get("result")


def values(schema):
    if isinstance(schema, dict):
        if "type" in schema and "value" in schema:
            return schema["value"] if schema["value"] is not None else schema.get("default")
        return {key: values(value) for key, value in schema.items()}
    return schema


if __name__ == "__main__":
    credentials = json.loads(Path("/home/deploy/file-media/credentials.json").read_text())
    session = request("/admin/api/session", {"password": credentials["filestash_admin_password"]})
    HEADERS["Authorization"] = "Bearer " + session["access_token"]
    config = values(request("/admin/api/config"))
    config["general"].update(host="files.vallterra.wiki", force_ssl=True)
    config.setdefault("features", {}).setdefault("video", {}).update(
        enable_transcoder=True, encoder="h264_vaapi")
    label = "Home share"
    backend = {"type": "samba", "host": "host.docker.internal", "port": "445",
               "share": "home-share", "path": "/home-share/"}
    config["connections"] = [dict(backend, label=label)]
    config["middleware"] = {
        "identity_provider": {"type": "passthrough", "params": json.dumps({
            "strategy": "username_and_password"})},
        "attribute_mapping": {"related_backend": label, "params": json.dumps({
            label: dict(backend, username="{{ .user }}", password="{{ .password }}")})},
    }
    request("/admin/api/config", config)
    public = request("/api/config")
    assert public["origin"] == "https://files.vallterra.wiki"
    assert public["auth"] == [label]
    print("Configured username/password login to the fixed Samba share.")
