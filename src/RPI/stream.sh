#!/bin/bash
libcamera-vid -t 0 --inline --width 640 --height 480 --framerate 24 -o - | cvlc -vvv stream:///dev/stdin --sout '#rtp{sdp=rtsp://:8554/stream}' :demux=h264
