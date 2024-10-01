import requests
import stomp
import json
import threading
import time

SERVER_URL = "http://localhost:8080"
WS_SERVER = "ws://localhost:8080/ws-endpoint"  # Adjust the WS endpoint as needed

def load_users_from_file(filename):
    with open(filename, 'r') as file:
        users = [User(*line.strip().split(':')) for line in file]
    return users

def signup(user):
    response = requests.post(f"{SERVER_URL}/auth/register", json={"username": user.username, "password": user.password})
    print(f"Signup response for {user.username}: {response.status_code}")
    return response.ok

def login(user):
    response = requests.post(f"{SERVER_URL}/auth/login", json={"username": user.username, "password": user.password})
    if response.ok:
        print(f"Login successful for {user.username}")
        user.token = response.json()['jwt']
        return True
    print(f"Login failed for {user.username}")
    return False

def create_room(token):
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{SERVER_URL}/room/create", headers=headers)
    print(f"create_room response: \n{response}")
    if response.ok:
        room_id = response.json()['roomId']
        print(f"Room created with ID: {room_id}")
        return room_id
    print("Failed to create room")
    return None

class User:
    def __init__(self, username, password, token=None):
        self.username = username
        self.password = password
        self.token = token


class StompClient(stomp.ConnectionListener):
    def __init__(self, user, room_id):
        self.user = user
        self.token = user.token
        self.room_id = room_id
        self.conn = None 

    def on_connected(self, frame):
        print(f"Connected to WebSocket, joining room {self.room_id}")
        self.send("/app/joinRoom", {"roomID": self.room_id, "gameType": "game"})

    def on_disconnected(self):
        print("Disconnected from WebSocket")

    def on_error(self, frame):
        print(f"Received an error: {frame.body}")

    def on_message(self, frame):
        print(f"Message received: {frame.body}")

    def subscribe_to_room_and_messages(self):
        destination = f"/topic/roomJoined/{self.room_id}"
        self.conn.subscribe(destination=destination, id=1, ack='auto')
        self.conn.subscribe(destination="/user/queue/privateMessage", id=1, ack='auto')
        print(f"Subscribed {self.user.username} to the channels")

    

    def connect_and_listen(self):
        host = "localhost"
        port = 8080
        self.conn = stomp.Connection12(host_and_ports=[(host, port)])
        self.conn.set_listener('', self)
        self.conn.connect(headers={"token": self.token}, wait=True)
        threading.Thread(target=self.send_heartbeat, daemon=True).start()

    def send(self, destination, body):
        self.conn.send(destination=destination, body=json.dumps(body), headers={"token": self.token})

    def send_heartbeat(self):
        while self.conn.is_connected():
            time.sleep(5)
            self.send("/app/heartbeat", {"heartbeatMessage": "hb"})
            print(f"Heartbeat sent for room {self.room_id}")

def main():
    users = load_users_from_file("users.txt")
    
    for i in range(0, len(users), 10):
        group = users[i:i+10]
        
        for user in group:
            if signup(user) and login(user):
                continue
            else:
                group.remove(user) 
        
        if group:
            room_id = create_room(group[0].token)
            if room_id:
                for user in group:
                    client = StompClient(user, room_id)
                    client.connect_and_listen()
                    print(f"{user.username} attempting to join room {room_id}.")


if __name__ == "__main__":
    main()
