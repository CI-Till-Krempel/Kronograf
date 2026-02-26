
from flask import Flask, request, jsonify
from .services.user_service import UserService

app = Flask(__name__)
user_service = UserService()

@app.route("/register", methods=["POST"])
def register():
    data = request.get_json()
    email = data.get("email")
    password = data.get("password")

    if not email or not password:
        return jsonify({"error": "Email and password are required."}), 400

    try:
        user = user_service.create_user(email, password)
        return jsonify({"message": "User created successfully.", "user_id": user.id}), 201
    except ValueError as e:
        return jsonify({"error": str(e)}), 409

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    email = data.get("email")
    password = data.get("password")

    user = user_service.verify_user(email, password)
    if user:
        # In a real app, we would issue a session token here.
        return jsonify({"message": "Login successful.", "user_id": user.id})
    
    return jsonify({"error": "Invalid credentials."}), 401

@app.route("/logout", methods=["POST"])
def logout():
    # In a real app, we would invalidate the session token here.
    return jsonify({"message": "Logout successful."})

if __name__ == "__main__":
    app.run(debug=True)
