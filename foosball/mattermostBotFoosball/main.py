import threading
from datetime import timedelta, datetime
from mmpy_bot import Bot, Settings
from attachment_generator import attach, mode_generator
from my_plugins import MyPlugin
from flask import Flask, jsonify
from supabase import create_client, Client
from config import (
    MATTERMOST_URL,
    MATTERMOST_PORT,
    BOT_TOKEN,
    BOT_TEAM,
    WEBHOOK_HOST_PORT,
    WEBHOOK_HOST_URL,
    SUPABASE_URL,
    SUPABASE_KEY, FOOSBALL_CHANNEL,
)

bot = Bot(
    settings=Settings(
        MATTERMOST_URL=MATTERMOST_URL,
        MATTERMOST_PORT=MATTERMOST_PORT,
        BOT_TOKEN=BOT_TOKEN,
        BOT_TEAM=BOT_TEAM,
        SSL_VERIFY=False,
        WEBHOOK_HOST_ENABLED=True,
        WEBHOOK_HOST_PORT=WEBHOOK_HOST_PORT,
        WEBHOOK_HOST_URL=WEBHOOK_HOST_URL,
    ),
    plugins=[MyPlugin()],
)

app = Flask(__name__)

supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)


def statistics_parser(statistic: list):
    res = {
        "attachments": [
            {
                "pretext": "Statistics of players",
                "fields": [
                ],
            }
        ],
        "response_type": "in_channel"
    }
    for i in range(len(statistic)):
        field = {
            "title": f"{i+1}.{statistic[i]["name"].capitalize()}",
            "value": f"elo:{statistic[i]["elo"]} games:{statistic[i]["games"]}",
        }
        res["attachments"][0]["fields"].append(field)
    return res


def game_parser(games: list):
    message = ""
    for i in range(len(games)):
        blue_member_1 = games[i]["blueTeamMember1"]
        blue_member_2 = games[i]["blueTeamMember2"]
        red_member_1 = games[i]["redTeamMember1"]
        red_member_2 = games[i]["redTeamMember2"]
        score_red = games[i]["scoreRed"]
        score_blue = games[i]["scoreBlue"]
        message += (f"{blue_member_1} and {blue_member_2} played vs {red_member_1} and {red_member_2}\n"
                    f"with score {score_blue}:{score_red}\n\n")
    return message


@app.post("/history")
def history():
    twenty_four_hours_ago = datetime.utcnow() - timedelta(days=7)
    timestamp_str = twenty_four_hours_ago.strftime('%Y-%m-%d %H:%M:%S')
    games = supabase.table("game_scores").select("*").filter("gameDate", "gte", timestamp_str).execute().data
    games_info = game_parser(games)
    return games_info


@app.get("/db")
def db():
    last_game = supabase.table("game_scores").select("*").order(column="gameId", desc=True).limit(1).execute().data
    game_info = game_parser(last_game)
    bot.driver.create_post(channel_id=FOOSBALL_CHANNEL, message=game_info+"Congrats to the winners!")
    return "Last game sent"


@app.post("/statistics")
def statistics():
    statistic = supabase.table("rating").select("*").order(column="elo", desc=True).execute().data
    statistics_info = statistics_parser(statistic)
    return jsonify(statistics_info)


@app.post("/quick_game")
def check():
    data = attach()
    return jsonify(data)


@app.post("/mode")
def mode():
    data = mode_generator()
    jsonify(data)
    return jsonify(data)


def run_flask():
    app.run()


if __name__ == "__main__":
    web_server = threading.Thread(target=run_flask)
    web_server.start()
    bot.run()
