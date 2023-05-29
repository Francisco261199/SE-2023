import smbus2 
import time
import sys

DEVICE_ADDRESS = 0x42

bus = smbus2.SMBus(1)
time.sleep(1)  #wait here to avoid 121 IO Error

while True:

	response = bus.read_i2c_block_data(DEVICE_ADDRESS, 0, 30)

	received_data = ''.join(chr(byte) for byte in response)
	if received_data.startswith('sensor') or received_data.startswith('ring'):
		print(received_data)
		sys.stdout.flush()
	time.sleep(0.25)