
from .models.user import User
import bcrypt

# A simple in-memory store for users for demonstration purposes
_users = {}


class UserService:
    def create_user(self, email, password):
        if email in [user.email for user in _users.values()]:
            raise ValueError("User with that email already exists.")

        hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
        new_user = User(email=email, password_hash=hashed_password.decode('utf-8'))
        _users[new_user.id] = new_user
        return new_user

    def verify_user(self, email, password):
        user = next((u for u in _users.values() if u.email == email), None)
        if user and bcrypt.checkpw(password.encode('utf-8'), user.password_hash.encode('utf-8')):
            return user
        return None

    def find_by_id(self, user_id):
        return _users.get(user_id)

