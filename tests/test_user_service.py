
import unittest
from app.services.user_service import UserService

class TestUserService(unittest.TestCase):

    def setUp(self):
        # Reset the in-memory store before each test
        from app.services import user_service
        user_service._users = {}
        self.user_service = UserService()

    def test_create_user(self):
        email = "test@example.com"
        password = "password123"
        user = self.user_service.create_user(email, password)
        self.assertIsNotNone(user)
        self.assertEqual(user.email, email)
        self.assertNotEqual(user.password_hash, password)

    def test_create_duplicate_user(self):
        email = "test@example.com"
        password = "password123"
        self.user_service.create_user(email, password)
        with self.assertRaises(ValueError):
            self.user_service.create_user(email, "anotherpassword")

    def test_verify_user_success(self):
        email = "test@example.com"
        password = "password123"
        self.user_service.create_user(email, password)
        verified_user = self.user_service.verify_user(email, password)
        self.assertIsNotNone(verified_user)
        self.assertEqual(verified_user.email, email)

    def test_verify_user_failure(self):
        email = "test@example.com"
        password = "password123"
        self.user_service.create_user(email, password)
        verified_user = self.user_service.verify_user(email, "wrongpassword")
        self.assertIsNone(verified_user)

if __name__ == '__main__':
    unittest.main()
