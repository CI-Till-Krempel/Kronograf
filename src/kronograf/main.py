from .timer import Timer


def main():
    """Main application loop."""
    timer = Timer()
    print("Kronograf Time Tracker")
    print("Commands: start, stop, describe <text>, status, exit")

    while True:
        try:
            command_line = input("> ").strip().split(maxsplit=1)
            if not command_line:
                continue

            command = command_line[0].lower()
            args = command_line[1] if len(command_line) > 1 else ""

            if command == "start":
                print(timer.start())
            elif command == "stop":
                print(timer.stop())
            elif command == "describe":
                if not args:
                    print("Error: Please provide a description.")
                else:
                    print(timer.describe(args))
            elif command == "status":
                print(timer.get_status())
            elif command == "exit":
                print("Exiting Kronograf.")
                break
            else:
                print(f"Unknown command: {command}")

        except (EOFError, KeyboardInterrupt):
            print("\nExiting Kronograf.")
            break


if __name__ == "__main__":
    main()
