const os = require('os');
const path = require('path');
const { spawn } = require('child_process')


var handler = require('../controllers/handler_rec')
var notify = require('../routes/notification');
const notification = require('../routes/notification');

function runRecordingHandler(handlerObj) {

    const pythonProcess = spawn('python3', ['../../scripts/receivearduino.py']);
    console.log("Server listening to Arduino messages")

    pythonProcess.stdout.on('data', (data) => {
        const output = data.toString().partition('!')[0]
        console.log("received message from Arduino: "+output)
        if (handlerObj != null) {
            if (output === 'sensor') {
                console.log("Receiving sensor data")
                handlerObj.startTime = Date.now()
                if (handlerObj.intervalId != null) {
                    clearInterval(handlerObj.intervalId)
                }
                const interval = setInterval(() => {
                    console.log("RecordingHandlerObj: "+ JSON.stringify(handlerObj))
                    var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
                    console.log("Elapsed time: "+ elapsed_time)
                    if (elapsed_time > 1) {
                        handlerObj.Rec.kill()
                        handlerObj.RecON = false
                        handlerObj.Rec = null
                        handlerObj.startTime = 0
                        //turn off led
                    }
                }, 5000)
                handlerObj.intervalId = interval
                handler.initRec(handlerObj)
                notification.sendNotification("Sensor Sensor Sensor!", "Movement was detected in your doorbell!")
                //turn on LED
            } else if (output === 'ring') {
                if (handlerObj.intervalId == null) {
                    const interval = setInterval(() => {
                        console.log("RecordingHandlerObj: "+ JSON.stringify(handlerObj))
                        var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
                        console.log("Elapsed time: "+ elapsed_time)
                        if (elapsed_time > 1) {
                            handlerObj.Rec.kill()
                            handlerObj.RecON = false
                            handlerObj.Rec = null
                            handlerObj.startTime = 0
                        }
                    }, 5000)
                    handlerObj.intervalId = interval
                }
                console.log("Receiving ring data")
                handler.initRec(handlerObj)
                notification.sendNotification("Ding Ding Ding!", "Looks like someone rung your doorbell!")
            }
        }
    });
}

module.exports = {
    runRecordingHandler,
}                