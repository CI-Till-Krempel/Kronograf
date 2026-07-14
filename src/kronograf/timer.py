from dataclasses import dataclass, field
import time
from typing import Optional


@dataclass
class TimeEntry:
    """Represents a single entry of tracked time."""
    start_time: Optional[float] = None
    end_time: Optional[float] = None
    description: str = ""

    @property
    def elapsed_time(self) -> float:
        """Calculate the elapsed time in seconds."""
        if self.start_time and self.end_time:
            return self.end_time - self.start_time
        elif self.start_time:
            return time.time() - self.start_time
        return 0.0


class Timer:
    """Manages the time-tracking state."""

    def __init__(self):
        self.current_entry: Optional[TimeEntry] = None

    def start(self) -> str:
        """Starts a new time entry."""
        if self.current_entry and self.current_entry.start_time and not self.current_entry.end_time:
            return "Timer is already running."
        self.current_entry = TimeEntry(start_time=time.time())
        return "Timer started."

    def stop(self) -> str:
        """Stops the current time entry."""
        if not self.current_entry or self.current_entry.end_time:
            return "No timer is currently running."
        self.current_entry.end_time = time.time()
        return f"Timer stopped. Elapsed time: {self.current_entry.elapsed_time:.2f} seconds."

    def describe(self, description: str) -> str:
        """Adds a description to the current entry."""
        if not self.current_entry:
            return "No timer has been started yet."
        self.current_entry.description = description
        return "Description added."

    def get_status(self) -> str:
        """Returns the current status of the timer."""
        if not self.current_entry:
            return "No timer started."
        if not self.current_entry.end_time:
            return f"Timer running for {self.current_entry.elapsed_time:.2f} seconds."
        return f"Timer stopped. Last entry duration: {self.current_entry.elapsed_time:.2f} seconds."
