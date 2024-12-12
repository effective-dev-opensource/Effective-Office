import os

MATTERMOST_URL = os.environ["MATTERMOST_URL"]
MATTERMOST_PORT = int(os.environ["MATTERMOST_PORT"])
BOT_TOKEN = os.environ["MM_TOKEN"]
BOT_TEAM = os.environ["BOT_TEAM"]
WEBHOOK_HOST_PORT = int(os.environ["WEBHOOK_HOST_PORT"])
WEBHOOK_HOST_URL = os.environ["WEBHOOK_HOST_URL"]
WEBHOOK_HOST = os.environ["WEBHOOK_HOST"]  # For attachment generator
WEBHOOK_EXTERNAL_PORT = os.environ["WEBHOOK_EXTERNAL_PORT"]  # For attachment generator
SUPABASE_URL = os.environ["SUPABASE_URL"]
SUPABASE_KEY = os.environ["SUPABASE_KEY"]
FOOSBALL_CHANNEL = os.environ["FOOSBALL_CHANNEL"]
