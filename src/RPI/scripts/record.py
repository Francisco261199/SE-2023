import os
import datetime

from picamera2 import Picamera2
from picamera2.encoders import H264Encoder

OUTPUT_FOLDER = '/home/camera/camera/SE-2023/src/RPI/multimedia/'
MAX_DURATION_RECORDING = 60 * 15 #15min; to prevent storage from getting full

os.makedirs(OUTPUT_FOLDER, exist_ok=True)
current_time = datetime.datetime.now().strftime('%Y%m%d_%H%M%S')
video_id = "recording"+current_time+".h264"

picam2 = Picamera2()
video_config = picam2.create_video_configuration({"size": (1280, 720)})
picam2.set_controls({"FrameRate": 10}) #STOP-MOTION
picam2.configure(video_config)

picam2.start_and_record_video(OUTPUT_FOLDER+video_id, duration=MAX_DURATION_RECORDING)