import unittest
import time
from src.kronograf.timer import Timer, TimeEntry


class TestTimer(unittest.TestCase):

    def setUp(self):
        """Set up a new Timer instance before each test."""
        self.timer = Timer()

    def test_initial_state(self):
        """Test the initial state of the timer."""
        self.assertIsNone(self.timer.current_entry)
        self.assertEqual(self.timer.get_status(), "No timer started.")
        self.assertEqual(self.timer.stop(), "No timer is currently running.")

    def test_start_timer(self):
        """Test starting the timer."""
        self.assertEqual(self.timer.start(), "Timer started.")
        self.assertIsNotNone(self.timer.current_entry)
        self.assertIsInstance(self.timer.current_entry, TimeEntry)
        self.assertIsNotNone(self.timer.current_entry.start_time)
        self.assertIsNone(self.timer.current_entry.end_time)
        # Cannot start a running timer
        self.assertEqual(self.timer.start(), "Timer is already running.")

    def test_stop_timer(self):
        """Test stopping the timer."""
        self.timer.start()
        time.sleep(0.1)  # Simulate some time passing
        stop_message = self.timer.stop()
        self.assertTrue(stop_message.startswith("Timer stopped."))
        self.assertIsNotNone(self.timer.current_entry.end_time)
        self.assertGreater(self.timer.current_entry.elapsed_time, 0)
        # Cannot stop a stopped timer
        self.assertEqual(self.timer.stop(), "No timer is currently running.")

    def test_describe_entry(self):
        """Test adding a description to a time entry."""
        self.assertEqual(self.timer.describe("test"), "No timer has been started yet.")
        self.timer.start()
        description = "This is a test description."
        self.assertEqual(self.timer.describe(description), "Description added.")
        self.assertEqual(self.timer.current_entry.description, description)

    def test_status_running(self):
        """Test the status of a running timer."""
        self.timer.start()
        self.assertTrue(self.timer.get_status().startswith("Timer running for"))

    def test_status_stopped(self):
        """Test the status of a stopped timer."""
        self.timer.start()
        self.timer.stop()
        self.assertTrue(self.timer.get_status().startswith("Timer stopped. Last entry duration"))


if __name__ == '__main__':
    unittest.main()
