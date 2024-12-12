from config import WEBHOOK_HOST, WEBHOOK_EXTERNAL_PORT


def attach():
    msg_body = {
        "attachments": [
            {
                "pretext": "Quick game.",
                "text": "Choose your position.",
                "actions": [
                    {
                        "id": "11",
                        "type": "button",
                        "name": "Goalkeeper",
                        "style": "primary",
                        "integration": {

                            "url": f"{WEBHOOK_HOST}:{WEBHOOK_EXTERNAL_PORT}/hooks/choose_position",
                            "context": dict(position=0),
                        },
                    },
                    {
                        "id": "12",
                        "type": "button",
                        "name": "Forward",
                        "style": "primary",
                        "integration": {
                            "url": f"{WEBHOOK_HOST}:{WEBHOOK_EXTERNAL_PORT}/hooks/choose_position",
                            "context": dict(position=1),
                        },
                    },
                    {
                        "id": "21",
                        "type": "button",
                        "name": "Forward",
                        "style": "danger",
                        "integration": {
                            "url": f"{WEBHOOK_HOST}:{WEBHOOK_EXTERNAL_PORT}/hooks/choose_position",
                            "context": dict(position=2),
                        },
                    },
                    {
                        "id": "22",
                        "type": "button",
                        "name": "Goalkeeper",
                        "style": "danger",
                        "integration": {
                            "url": f"{WEBHOOK_HOST}:{WEBHOOK_EXTERNAL_PORT}/hooks/choose_position",
                            "context": dict(position=3),
                        },
                    },
                ],
            }
        ],
        "props": {"kicker_positions": ["", "", "", ""]},
        "response_type": "in_channel",
    }
    return msg_body


def mode_generator():
    msg_body = {
        "attachments": [
            {
                "text": "Choose your mode.",
                "actions": [
                    {
                        "id": "11",
                        "type": "button",
                        "name": "Quick game",
                        "style": "primary",
                        "integration": {
                            "url": f"{WEBHOOK_HOST}:{WEBHOOK_EXTERNAL_PORT}/hooks/quick",
                        },
                    },
                    {
                        "id": "12",
                        "type": "button",
                        "name": "Matchmaking",
                        "style": "primary",
                        "integration": {
                            "url": f"{WEBHOOK_HOST}:{WEBHOOK_EXTERNAL_PORT}/hooks/matchmaking",
                        },
                    },
                    {
                        "id": "21",
                        "type": "button",
                        "name": "Tournament",
                        "style": "danger",
                    },
                ],
            }
        ],
        "response_type": "in_channel",
    }
    return msg_body
