from flask import Flask, render_template, request, redirect, url_for, flash
import json
import os
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)
app.config['SECRET_KEY'] = 'a-very-secret-key'  # In a real app, use a proper secret key
USERS_FILE = 'users.json'

def get_users():
    if not os.path.exists(USERS_FILE):
        return {}
    with open(USERS_FILE, 'r') as f:
        return json.load(f)

def save_user(user_data):
    users = get_users()
    email = user_data['email']
    if email in users:
        raise ValueError("User with this email already exists.")
    
    users[email] = {
        'password': generate_password_hash(user_data['password'])
    }
    
    with open(USERS_FILE, 'w') as f:
        json.dump(users, f, indent=4)

@app.route('/')
def index():
    return "Welcome to the User Authentication Service"

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        email = request.form.get('email')
        password = request.form.get('password')

        if not email or not password:
            flash('Email and password are required.')
            return redirect(url_for('register'))

        if len(password) < 8:
            flash('Password must be at least 8 characters long.')
            return redirect(url_for('register'))

        try:
            save_user({'email': email, 'password': password})
            # Simulate sending a confirmation email
            print(f"Confirmation email sent to {email}")
            flash('Registration successful! Please log in.')
            # In a real app, you might redirect to a login page
            # For this story, we can just confirm success.
            return f"User {email} registered successfully."
        except ValueError as e:
            flash(str(e))
            return redirect(url_for('register'))

    return render_template('register.html')

if __name__ == '__main__':
    app.run(debug=True)
