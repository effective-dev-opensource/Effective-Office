from mmpy_bot import Plugin, listen_webhook, WebHookEvent
from attachment_generator import attach


class MyPlugin(Plugin):
    def player_message_generator(self, msg: list):
        players_list = (
            f"Game created!\n"
            f"Player:{self.driver.get_user_info(msg[0])["username"]}\n"
            f"Player:{self.driver.get_user_info(msg[1])["username"]}\n"
            f"Player:{self.driver.get_user_info(msg[2])["username"]}\n"
            f"Player:{self.driver.get_user_info(msg[3])["username"]}"
        )
        return players_list

    def attach_change(self, msg_body, kicker_positions, event):
        if event.body["user_id"] in kicker_positions:
            kicker_positions[kicker_positions.index(event.body["user_id"])] = ""
            kicker_positions[event.body["context"]["position"]] = event.body["user_id"]
        kicker_positions[event.body["context"]["position"]] = event.body["user_id"]
        for i in range(0, 4):
            if kicker_positions[i] != "":
                msg_body["attachments"][0]["actions"][i][
                    "name"
                ] = self.driver.get_user_info(kicker_positions[i])[
                    "username"
                ]  # Change name of the button
                msg_body["attachments"][0]["actions"][i][
                    "integration"
                ] = {}  # Integration
                msg_body["attachments"][0]["actions"][i]["style"] = "default"  # Color
                msg_body["props"]["kicker_positions"][i] = kicker_positions[i]
        return msg_body

    @listen_webhook("quick")
    async def quick_listener(self, event: WebHookEvent):
        msg_body = attach()
        self.driver.respond_to_web(
            event,
            {
                "update": {
                    "message": "Mode chosen!",
                    "props": {
                        "attachments": msg_body["attachments"],
                        "kicker_positions": msg_body["props"]["kicker_positions"],
                    },
                },
            },
        )

    @listen_webhook("matchmaking")
    async def matchmaking_listener(self, event: WebHookEvent):
        msg_body = attach()
        msg_body["attachments"][0]["pretext"] = "Matchmaking"
        self.driver.respond_to_web(
            event,
            {
                "update": {
                    "message": "Mode chosen!",
                    "props": {
                        "attachments": msg_body["attachments"],
                        "kicker_positions": msg_body["props"]["kicker_positions"],
                    },
                },
            },
        )

    @listen_webhook("choose_position")
    async def form_listener(self, event: WebHookEvent):
        kicker_positions = self.driver.get_post_thread(event.body["post_id"])["posts"][
            event.body["post_id"]
        ]["props"]["kicker_positions"]
        msg_body = attach()
        msg_body = self.attach_change(msg_body, kicker_positions, event)
        if "" not in msg_body["props"]["kicker_positions"]:
            players_list = self.player_message_generator(
                msg_body["props"]["kicker_positions"]
            )
            self.driver.respond_to_web(
                event,
                {
                    "update": {
                        "message": players_list,
                        "props": {},
                    }
                },
            )

        self.driver.respond_to_web(
            event,
            {
                "update": {
                    "props": {
                        "attachments": msg_body["attachments"],
                        "kicker_positions": msg_body["props"]["kicker_positions"],
                    },
                },
            },
        )
