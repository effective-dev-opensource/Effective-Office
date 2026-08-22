#!/usr/bin/env python3
"""Make bookings of the listed owners look like somebody else's, so they cannot be edited.

The dummy calendar provider never sets isEditable, the REST API has no field for it, and
`BookingDto` defaults it to true — so on a local stand every booking looks like your own
and the case "a booking somebody else owns must not open the editor" cannot be staged at
all. This sits between the client and the backend and rewrites the flag on the way back,
which needs no change to the repository and no backend restart.

    localQuickStart/editable-proxy.py --external min.kim@example.com
    localQuickStart/editable-proxy.py --external a@b.c --external d@e.f --port 8082

The client has to be built against the proxy port, because the URL is compiled in:
`api.url.debug` becomes http://localhost:8081 for the iOS simulator and
http://10.0.2.2:8081 for the Android emulator. point-client-at-local.sh writes port 8080,
so edit the line after running it, and rebuild.

A booking marked this way is indistinguishable on screen — no colour, no icon, it simply
stops responding to touch. That is the app's behaviour, not a fault of the proxy.
"""

import argparse
import json
import urllib.error
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

# Hop-by-hop headers plus the two that stop being true once the body is rewritten.
SKIP_HEADERS = {
    "connection", "keep-alive", "transfer-encoding", "te", "trailer",
    "proxy-authorization", "proxy-authenticate", "upgrade",
    "content-length", "content-encoding",
}


def mark_external(node, external):
    if isinstance(node, list):
        for item in node:
            mark_external(item, external)
    elif isinstance(node, dict):
        if "isEditable" in node and "owner" in node:
            owner = node.get("owner") or {}
            if owner.get("email") in external:
                node["isEditable"] = False
        for value in node.values():
            mark_external(value, external)


def make_handler(target, external):
    class Handler(BaseHTTPRequestHandler):
        protocol_version = "HTTP/1.1"

        def log_message(self, *args):
            pass

        def proxy(self):
            length = int(self.headers.get("Content-Length") or 0)
            body = self.rfile.read(length) if length else None
            headers = {
                k: v for k, v in self.headers.items()
                if k.lower() not in SKIP_HEADERS and k.lower() != "accept-encoding"
            }
            headers["Accept-Encoding"] = "identity"
            request = urllib.request.Request(
                target + self.path, data=body, headers=headers, method=self.command
            )
            try:
                response = urllib.request.urlopen(request)
                code, raw, response_headers = response.status, response.read(), response.headers
            except urllib.error.HTTPError as error:
                code, raw, response_headers = error.code, error.read(), error.headers
            except OSError as error:
                self.send_error(502, str(error))
                return

            if raw and "json" in response_headers.get("Content-Type", ""):
                try:
                    data = json.loads(raw)
                    mark_external(data, external)
                    raw = json.dumps(data).encode()
                except ValueError:
                    pass

            self.send_response(code)
            for key, value in response_headers.items():
                if key.lower() not in SKIP_HEADERS:
                    self.send_header(key, value)
            self.send_header("Content-Length", str(len(raw)))
            self.end_headers()
            if raw:
                self.wfile.write(raw)

        do_GET = do_POST = do_PUT = do_DELETE = do_PATCH = proxy

    return Handler


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument(
        "--external", action="append", default=[], metavar="EMAIL",
        help="owner whose bookings become read-only; repeat for several",
    )
    parser.add_argument("--host", default="0.0.0.0")
    parser.add_argument("--port", type=int, default=8081)
    parser.add_argument("--target-host", default="127.0.0.1")
    parser.add_argument("--target-port", type=int, default=8080)
    args = parser.parse_args()

    external = set(args.external)
    if not external:
        parser.error("nothing to mark: pass at least one --external EMAIL")

    target = f"http://{args.target_host}:{args.target_port}"
    server = ThreadingHTTPServer((args.host, args.port), make_handler(target, external))
    print(f"listening on {args.host}:{args.port} -> {target}")
    print(f"read-only for: {', '.join(sorted(external))}")
    print("point api.url.debug at this port and rebuild the client", flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("stopped", flush=True)


if __name__ == "__main__":
    main()
