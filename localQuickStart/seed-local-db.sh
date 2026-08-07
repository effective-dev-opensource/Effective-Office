#!/usr/bin/env bash
# Seed the local Postgres so the tablet app has something to show:
#   - an API key the clients authenticate with (Bearer token)
#   - one workspace zone + two meeting rooms (Sync, Focus)
#
# The backend stores API keys as the lowercase hex SHA-256 of the raw key.
# The raw key below MUST match `apiKey` in the repo-root local.properties.
#
# Safe to re-run (idempotent). Requires the postgres-effectiveoffice container to be running.
set -euo pipefail

CONTAINER="${PG_CONTAINER:-postgres-effectiveoffice}"
API_KEY="${LOCAL_API_KEY:-effective-office-local-key}"

# NOTE: docker exec needs no stdin here because we pass SQL via -c.
psql() { docker exec "$CONTAINER" psql -U postgres -d effectiveoffice -v ON_ERROR_STOP=1 "$@"; }

HASH="$(printf '%s' "$API_KEY" | shasum -a 256 | awk '{print $1}')"
echo "Seeding API key '$API_KEY'  (sha256=$HASH)"

psql -c "INSERT INTO api_keys (id, key_value, description)
         VALUES (gen_random_uuid(), '$HASH', 'local dev key')
         ON CONFLICT (key_value) DO NOTHING;"

psql -c "INSERT INTO workspace_zones(id,name) VALUES (gen_random_uuid(),'Ground floor')
         ON CONFLICT (name) DO NOTHING;"

for room in Sync Focus; do
  psql -c "INSERT INTO workspaces(id,name,tag,zone_id)
           SELECT gen_random_uuid(),'$room','meeting', z.id
           FROM workspace_zones z WHERE z.name='Ground floor'
           ON CONFLICT (name) DO NOTHING;"
done

echo "--- current meeting rooms ---"
psql -c "SELECT w.name, w.tag, z.name AS zone
         FROM workspaces w LEFT JOIN workspace_zones z ON z.id=w.zone_id
         WHERE w.tag='meeting';"

# Organizers = the booking editor's "Choose organizer" list.
# NOTE: the tablet client filters users by tag == "employer" (a typo in
# OrganizerRepositoryImpl — everything else uses "employee"), and the backend
# ignores the user_tag query param, so the DB tag must be 'employer' to show up.
# Enough of them to fill the list past its 150.dp cap, or scrolling cannot be looked at: with
# three names there is nothing to scroll and no way to tell a scrolling bug from a still list.
# Names vary in length on purpose, so text that wraps or gets clipped shows up here too.
for row in "jdoe|john.doe@example.com|John|Doe" \
           "asmith|anna.smith@example.com|Anna|Smith" \
           "mkim|min.kim@example.com|Min|Kim" \
           "pivanov|pyotr.ivanov@example.com|Пётр|Иванов" \
           "esokolova|elena.sokolova@example.com|Елена|Соколова" \
           "dvolkov|dmitry.volkov@example.com|Дмитрий|Волков" \
           "akonstantinopolskaya|a.konst@example.com|Анастасия|Константинопольская" \
           "ilee|isabella.lee@example.com|Isabella|Lee" \
           "mokonkwo|m.okonkwo@example.com|Chukwuemeka|Okonkwo" \
           "ttanaka|t.tanaka@example.com|Takeshi|Tanaka" \
           "sgarcia|s.garcia@example.com|Sofia|Garcia" \
           "obrown|o.brown@example.com|Oliver|Brown" \
           "nnovak|n.novak@example.com|Nina|Novak" \
           "rmuller|r.muller@example.com|Rudolf|Müller" \
           "ksmirnova|k.smirnova@example.com|Ксения|Смирнова" \
           "vpetrov|v.petrov@example.com|Владимир|Петров" \
           "ahassan|a.hassan@example.com|Amina|Hassan" \
           "lrossi|l.rossi@example.com|Luca|Rossi" \
           "ykim|y.kim@example.com|Yuna|Kim" \
           "bandersson|b.andersson@example.com|Bjorn|Andersson"; do
  IFS='|' read -r username email first last <<< "$row"
  psql -c "INSERT INTO users (id, username, email, first_name, last_name, created_at, updated_at, active, role, tag)
           VALUES (gen_random_uuid(), '$username', '$email', '$first', '$last', now(), now(), true, 'employer', 'employer')
           ON CONFLICT (email) DO NOTHING;"
done
# Fix any pre-existing rows seeded with the wrong tag.
psql -c "UPDATE users SET tag='employer' WHERE tag='employee';"

echo "--- current organizers (tag=employer) ---"
psql -c "SELECT first_name, last_name, email FROM users WHERE tag='employer';"
echo "Done."
