#!/usr/bin/env bash
# Local, fully-offline backend run for Effective-Office.
#
# All external providers are switched to dummy/in-memory so NO real
# Google / Firebase / Synology / Notion / Clockify / Mattermost accounts are needed.
# See ./README.md in this folder for the full story and the client steps.
#
# Prereqs: Postgres running on localhost:5432 (db=effectiveoffice, user/pass=postgres):
#   docker run --name postgres-effectiveoffice -e POSTGRES_DB=effectiveoffice \
#     -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15-alpine
#
# Usage (from anywhere):
#   localQuickStart/run-backend-local.sh
set -euo pipefail

# This script lives in <repo>/localQuickStart ; gradlew lives in the repo root.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

# --- fake Google/Firebase service-account json (valid RSA key, never leaves the machine) ---
# FirebaseConfig reads this file at startup unconditionally, so it must parse — but with the
# dummy calendar provider active it is never actually used to talk to Google.
CRED_FILE="$SCRIPT_DIR/.local-fake-credentials.json"
if [ ! -f "$CRED_FILE" ]; then
  KEY=$(openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 2>/dev/null | python3 -c 'import sys;print(sys.stdin.read().replace(chr(10),"\\n"),end="")')
  cat > "$CRED_FILE" <<EOF
{"type":"service_account","project_id":"effective-office-local","private_key_id":"0000000000000000000000000000000000000000","private_key":"${KEY}","client_email":"local@effective-office-local.iam.gserviceaccount.com","client_id":"000000000000000000000","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_x509_cert_url":"https://example.com/x509"}
EOF
fi
STUB="file:${CRED_FILE}"

# --- datasource ---
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/effectiveoffice"
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export MIGRATIONS_ENABLE=true
export APPLICATION_URL="http://localhost:8080"

# --- switch every provider to the offline/dummy implementation ---
export CALENDAR_PROVIDER=dummy
export PHOTOS_PROVIDER=dummy
export TEAMMATES_PROVIDER=dummy
export SPORT_PROVIDER=dummy
# photo-saver has no clean dummy (its dummy bean name clashes with the photos dummy),
# so use the mattermost impl with the scheduler OFF -> beans load but never hit the network.
export PHOTO_SAVER_PROVIDER=mattermost
export PHOTO_SAVER_STORAGE=dummy
export PHOTO_SAVER_SCHEDULER_ENABLED=false

# --- calendar / firebase (dummy provider active, so these files are only parsed, never used) ---
export GOOGLE_CREDENTIALS_FILE="$STUB"
export FIREBASE_CREDENTIALS="$STUB"
export DEFAULT_CALENDAR=primary
export CALENDAR_APPLICATION_NAME=EffectiveOfficeLocal
export CALENDAR_DELEGATED_USER=local@example.com
export DEFAULT_APP_EMAIL=local@example.com
export CALENDARS=""
export TEST_APPLICATION_URL=""
export TEST_CALENDARS=""

# --- these provider config classes load unconditionally and read env directly,
#     so they must be non-empty even though the providers are dummy ---
export SYNOLOGY_IP=dummy
export SYNOLOGY_LOGIN=dummy
export SYNOLOGY_PASSWORD=dummy
export SYNOLOGY_ALBUM_NAME=dummy
export NOTION_TOKEN=dummy
export NOTION_TEAMMATES_DB_ID=dummy
export NOTION_SUPERNOVA_DB_ID=dummy
export CLOCKIFY_API_KEY=dummy
export CLOCKIFY_WORKSPACE_ID=dummy
export CLOCKIFY_PROJECT_ID=dummy
export PHOTO_SAVER_MATTERMOST_BASE_URL="http://localhost"
export PHOTO_SAVER_MATTERMOST_TOKEN=dummy
export PHOTO_SAVER_EMOJI_REQUEST_SAVE=star
export PHOTO_SAVER_EMOJI_SUCCESS=white_check_mark
export PHOTO_SAVER_SYNOLOGY_BASE_URL="http://localhost"
export PHOTO_SAVER_SYNOLOGY_USERNAME=dummy
export PHOTO_SAVER_SYNOLOGY_PASSWORD=dummy
export PHOTO_SAVER_ALBUM_NAME=dummy

# -PbuildVariant=upstream explicitly, never the default. The Aurora variant rewrites the module
# list down to clients:tablet, so with `buildVariant=aurora` left active in gradle.properties —
# which is how anyone working on the Aurora client leaves it — this line fails with "project
# 'backend' not found", and the backend simply never comes up. There is no Aurora build of the
# backend for the flag to ever mean anything else here.
exec ./gradlew -PbuildVariant=upstream :backend:app:bootRun --args='--spring.profiles.active=local' --console=plain
