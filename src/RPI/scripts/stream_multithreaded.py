import io
import socket
import struct
import threading
import sys
import time

from picamera2 import Picamera2
from picamera2.encoders import H264Encoder
from picamera2.outputs import FileOutput

if len(sys.argv) < 2 :
    print("Missing port number")
    sys.exit(1)

port = int(sys.argv[1])

def handle_client(client_socket):  
    stream = client_socket.makefile('wb')

    try:
        picam2.encoder.output = FileOutput(stream)
        picam2.encoder.output.start()
        while True:
            time.sleep(1)

    finally:
        picam2.stop()
        picam2.stop_encoder()
        stream.close()
        client_socket.close()

# Initialize the camera
picam2 = Picamera2()
video_config = picam2.create_video_configuration({"size": (1280, 720)})
picam2.configure(video_config) 
encoder = H264Encoder(repeat=True)
picam2.start_encoder(encoder)
picam2.start()

#picam2.set_controls({"FrameRate": 4})

# Configure the TCP server settings
host = '0.0.0.0'  # Interface to listen on (all available network interfaces)
port = int(port) # Port to listen on

# Create a TCP server socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((host, port))
server_socket.listen(0)

try:
    while True:
        client_socket, client_address = server_socket.accept()
        client_thread = threading.Thread(target=handle_client, args=(client_socket,))
        client_thread.start()

finally:
    server_socket.close()