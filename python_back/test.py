import requests

url = 'http://localhost:5000/ask'
data = {
    "question": "计算机网络有哪些题型",
    "user_id": 1
}

response = requests.post(url, json=data)

if response.status_code == 200:
    print(response.json())
else:
    print(f"请求失败，状态码: {response.status_code}")