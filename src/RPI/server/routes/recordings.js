const os = require('os');
const path = require('path');
const { spawn } = require('child_process')


var handler = require('../controllers/handler_rec')
var notify = require('../routes/notification');
const notification = require('../routes/notification');

var streamHandlerObj = null

function runRecordingHandler(handlerObj) {

    if ( !handlerObj.streamON ) {
        const pythonProcess = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/receivearduino.py']);
        console.log("Server listening to Arduino messages")

        pythonProcess.stdout.on('data', (data) => {
            const output = data.toString().split('!')[0]
            console.log("received message from Arduino: "+output)

            if (handlerObj != null || !handlerObj.streamON ) { //if handler is not set or stream occuring
                if (output === 'sensor') {
                    if (handlerObj.intervalId != null) {
                        console.log("Refresh recording timer")
                        clearInterval(handlerObj.intervalId)
                    }
                    if (!handlerObj.RecON) {
                        // send notification if not recording
                        notification.sendNotification("Alert!", "Movement was detected in your doorbell!")
                    }
                    handler.initRec(handlerObj)
                    //notification.sendNotification("Sensor Sensor Sensor!", "Movement was detected in your doorbell!")

                } else if (output === 'ring') {
                    handler.initRec(handlerObj)
                    notification.sendNotification("Ding Ding Ding!", "Looks like someone rung your doorbell!")
                }
            }
        });

        pythonProcess.on('error', (error) => {
            console.error('An error occurred while spawning the process:', error);
        });

        pythonProcess.on('close', (msg) => {
            console.error('Closed Arduino listener:', msg);
            console.log("Trying to reopen Arduino communication")
            runRecordingHandler(handlerObj)
        });
    } else if (handlerObj.streamON){
        console.log("Cannot start recording. Camera object locked because of occuring stream")
    }
}

module.exports = {
    runRecordingHandler,
    setStreamObj: function(initStreamObj) {
        streamHandlerObj = initStreamObj
    }
}                