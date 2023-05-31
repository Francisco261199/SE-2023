import os

def startService():
    try:
        os.popen("sudo systemctl start stream.service")
    #systemctl start stream.service
    except OSError as ose:
        print("error: "+ ose)
    
    pass

startService()