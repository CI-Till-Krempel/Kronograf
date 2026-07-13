import unittest
import os
import json
from app import app

class AuthTestCase(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.app.testing = True
        # Use a temporary file for users
        self.users_file = 'test_users.json'
        app.config['USERS_FILE'] = self.users_file

    def tearDown(self):
        if os.path.exists(self.users_file):
            os.remove(self.users_file)

    def test_register_page_loads(self):
        response = self.app.get('/register')
        self.assertEqual(response.status_code, 200)
        self.assertIn(b'Register New User', response.data)

    def test_successful_registration(self):
        response = self.app.post('/register', data=dict(
            email='test@example.com',
            password='password123'
        ), follow_redirects=True)
        self.assertEqual(response.status_code, 200)
        self.assertIn(b'User test@example.com registered successfully.', response.data)
        
        # Verify user was saved
        with open(self.users_file, 'r') as f:
            users = json.load(f)
            self.assertIn('test@example.com', users)

    def test_register_existing_user(self):
        # First, register a user
        self.app.post('/register', data=dict(
            email='test@example.com',
            password='password123'
        ), follow_redirects=True)

        # Try to register the same user again
        response = self.app.post('/register', data=dict(
            email='test@example.com',
            password='password123'
        ), follow_redirects=True)
        
        self.assertIn(b'User with this email already exists.', response.data)

    def test_register_short_password(self):
        response = self.app.post('/register', data=dict(
            email='test2@example.com',
            password='short'
        ), follow_redirects=True)
        self.assertIn(b'Password must be at least 8 characters long.', response.data)

if __name__ == '__main__':
    unittest.main()
