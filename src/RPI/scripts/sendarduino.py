import smbus2 
import time
import sys

DEVICE_ADDRESS = 0x42

bus = smbus2.SMBus(1)
time.sleep(1)  #wait here to avoid 121 IO Error

data = [ord(c) for c in sys.argv[1]]
bus.write_i2c_block_data(DEVICE_ADDRESS, 0, data)
