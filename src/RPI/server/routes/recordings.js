const os = require('os');
const path = require('path');
const { spawn } = require('child_process')


var handler = require('../controllers/handler_rec')
var notify = require('../routes/notification');
const notification = require('../routes/notification');

function runRecordingHandler(handlerObj) {

    const pythonProcess = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/receivearduino.py']);
    console.log("Server listening to Arduino messages")

    pythonProcess.stdout.on('data', (data) => {
        const output = data.toString().split('!')[0]
        console.log("received message from Arduino: "+output)
        
        if (handlerObj != null) {
            if (output === 'sensor') {
                if (handlerObj.intervalId != null) {
                    console.log("Refresh recording timer")
                    clearInterval(handlerObj.intervalId)
                }

                const interval = setInterval(() => {
                    console.log("startTime: "+ handlerObj.start)
                    var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
                    console.log("Elapsed time: "+ elapsed_time)
                    if (elapsed_time > 5) {
                        if (handlerObj.Rec != null) {
                            handlerObj.Rec.kill()
                        }
                        handlerObj.RecON = false
                        handlerObj.Rec = null
                        handlerObj.startTime = 0
                    }
                }, 5000)
                handlerObj.intervalId = interval
                handler.initRec(handlerObj)
                notification.sendNotification("Sensor Sensor Sensor!", "Movement was detected in your doorbell!")

            } else if (output === 'ring') {
                if (handlerObj.intervalId == null) {
                    const interval = setInterval(() => {
                        console.log("startTime: "+ handlerObj.start)
                        var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
                        console.log("Elapsed time: "+ elapsed_time)
                        if (elapsed_time > 5) {
                            if (handlerObj.Rec != null) {
                                handlerObj.Rec.kill()
                            }
                            
                            handlerObj.RecON = false
                            handlerObj.Rec = null
                            handlerObj.startTime = 0
                        }
                    }, 5000)
                    handlerObj.intervalId = interval
                }
                handler.initRec(handlerObj)
                notification.sendNotification("Ding Ding Ding!", "Looks like someone rung your doorbell!")
            }
        }
        else console.log("Error: handlerObj is null")
    });

    pythonProcess.on('error', (error) => {
        console.error('An error occurred while spawning the process:', error);
    });

    pythonProcess.on('close', (msg) => {
        console.error('Closed Arduino listener:', msg);
    });
}

module.exports = {
    runRecordingHandler,
}                