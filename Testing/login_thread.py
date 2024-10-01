import requests
import threading
import time

base_url = "http://localhost:8080/auth/"

def register_user(username, password, results, lock):
    url = base_url + "register"
    payload = {
        "username": username,
        "password": password
    }
    start_time = time.time()  
    try:
        response = requests.post(url, json=payload)
        end_time = time.time()  
        with lock:
            results.append((response.json(), end_time - start_time))  
        print(f"Register request for user {username} completed successfully.\n")
    except Exception as e:
        print(f"Error occurred during register request for user {username}: {e}\n")

def sign_in_user(username, password, results, lock):
    url = base_url + "login"
    payload = {
        "username": username,
        "password": password
    }
    start_time = time.time()  
    try:
        response = requests.post(url, json=payload)
        end_time = time.time()  
        with lock:
            results.append((response.json(), end_time - start_time))  
        print(f"Sign-in request for user {username} completed successfully.\n")
    except Exception as e:
        print(f"Error occurred during sign-in request for user {username}: {e}\n")


def generate_username(n):
    return f"loadtest{n}"

password = "password"

results = []
lock = threading.Lock() 

threads = []
for i in range(1, 1001):
    username = generate_username(i)
    thread = threading.Thread(target=register_user, args=(username, password, results, lock))
    threads.append(thread)
    thread.start()

for i in range(1, 1001):
    username = generate_username(i)
    thread = threading.Thread(target=sign_in_user, args=(username, password, results, lock))
    threads.append(thread)
    thread.start()

for thread in threads:
    thread.join()
    
for result, execution_time in results:
    print("Result:", result, "Execution time:", execution_time, "seconds")



