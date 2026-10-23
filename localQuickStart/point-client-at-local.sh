#!/usr/bin/env bash
# Point the tablet client at the local backend for one platform.
#
# The base URL is baked into the build and every platform reaches the host differently,
# so switching platforms means editing local.properties and rebuilding. Which line and
# which address is easy to get wrong, hence this script.
#
#   iOS simulator     shares the host network        -> api.url.debug=http://localhost:8080
#   Android emulator  host lives behind a special IP -> api.url.debug=http://10.0.2.2:8080
#   Aurora emulator   same IP as Android, but the build is release, so it reads api.url.release
#
# Usage (from anywhere):
#   localQuickStart/point-client-at-local.sh ios|android|aurora
#
# Rebuild after switching — the URL is compiled in.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROPS="$(cd "$SCRIPT_DIR/.." && pwd)/local.properties"

case "${1:-}" in
    ios)     KEY=api.url.debug   URL=http://localhost:8080 ;;
    android) KEY=api.url.debug   URL=http://10.0.2.2:8080 ;;
    aurora)  KEY=api.url.release URL=http://10.0.2.2:8080 ;;
    *)       echo "usage: $(basename "$0") ios|android|aurora" >&2; exit 2 ;;
esac

[ -f "$PROPS" ] || { echo "no $PROPS — see localQuickStart/README.md" >&2; exit 1; }
grep -q "^$KEY=" "$PROPS" || { echo "no $KEY in $PROPS" >&2; exit 1; }

# BSD sed and GNU sed disagree about -i, so write through a temporary file instead.
TMP="$(mktemp)"
sed "s|^$KEY=.*|$KEY=$URL|" "$PROPS" > "$TMP"
mv "$TMP" "$PROPS"

grep -E '^api\.url\.(debug|release)=' "$PROPS"
