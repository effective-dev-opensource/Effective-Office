#!/usr/bin/env python3
"""Put a delay in front of the local backend, so a request can be caught in flight.

Some cases need the user to act while a request is still unanswered, and the local
backend answers faster than a tap can land: closing quick booking mid-request, or
seeing the loader at all. This listens on 8081 and forwards to 8080, holding each
chunk on its way to the backend. Replies come back untouched.

    localQuickStart/slow-backend-proxy.py              # 12 s, the value the runs use
    localQuickStart/slow-backend-proxy.py --delay 3
    localQuickStart/slow-backend-proxy.py --port 8082 --target-port 8080

The delay is re-read from a control file on every chunk, so it can be changed while
the proxy runs — write 0 to let requests through at full speed without restarting.
The path is printed at startup.

The client has to be built against the proxy port, because the URL is compiled in:
`api.url.debug` becomes http://localhost:8081 for the iOS simulator and
http://10.0.2.2:8081 for the Android emulator. point-client-at-local.sh writes port
8080, so edit the line after running it, and rebuild.
"""

import argparse
import asyncio
import os
import tempfile

BUFFER_SIZE = 65536


def read_delay(path, fallback):
    try:
        with open(path) as f:
            return float(f.read().strip())
    except (OSError, ValueError):
        return fallback


async def pipe(reader, writer, control=None, fallback=0.0):
    try:
        while True:
            data = await reader.read(BUFFER_SIZE)
            if not data:
                break
            if control is not None:
                delay = read_delay(control, fallback)
                if delay > 0:
                    await asyncio.sleep(delay)
            writer.write(data)
            await writer.drain()
    except (ConnectionError, asyncio.IncompleteReadError):
        pass
    finally:
        writer.close()


async def serve(args):
    async def handle(client_reader, client_writer):
        try:
            backend_reader, backend_writer = await asyncio.open_connection(
                args.target_host, args.target_port
            )
        except OSError as error:
            print(f"cannot reach backend: {error}", flush=True)
            client_writer.close()
            return
        await asyncio.gather(
            pipe(client_reader, backend_writer, args.control, args.delay),
            pipe(backend_reader, client_writer),
        )

    server = await asyncio.start_server(handle, args.host, args.port)
    print(f"listening on {args.host}:{args.port} -> {args.target_host}:{args.target_port}")
    print(f"delay {args.delay} s, change it with: echo <seconds> > {args.control}")
    print("point api.url.debug at this port and rebuild the client", flush=True)
    async with server:
        await server.serve_forever()


def main():
    default_control = os.path.join(tempfile.gettempdir(), "effective-office-slow-delay")
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--delay", type=float, default=12.0, help="seconds to hold each chunk")
    parser.add_argument("--host", default="0.0.0.0")
    parser.add_argument("--port", type=int, default=8081)
    parser.add_argument("--target-host", default="127.0.0.1")
    parser.add_argument("--target-port", type=int, default=8080)
    parser.add_argument("--control", default=default_control, help="file the delay is re-read from")
    args = parser.parse_args()

    with open(args.control, "w") as f:
        f.write(str(args.delay))

    try:
        asyncio.run(serve(args))
    except KeyboardInterrupt:
        print("stopped", flush=True)


if __name__ == "__main__":
    main()
