import os

def stopService():
    try:
        os.popen("sudo systemctl stop stream.service")
    #systemctl start stream.service
    except OSError as ose:
        print("error: "+ ose)

    pass

stopService()