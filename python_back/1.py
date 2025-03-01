# First, check if the Ollama server is running and accessible
import ollama

client = ollama.Client(host="http://localhost:11434")  # Explicitly set the host

try:
    response = client.chat(model="deepseek-r1:latest", messages=[{'role': 'user', 'content': '你好'}])
    print(response)
except Exception as e:
    print("Chat error:", e)