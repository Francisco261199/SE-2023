const { spawn } = require('child_process')

module.exports.initRec = function (handlerObj){
    if(handlerObj != null) {
        if( !handlerObj.RecON ) {
            console.log("Init Recording")
            handlerObj.Rec = spawn('python3', ['../../scripts/recordings.py'])
            _ = spawn('python3', '../../scripts/sendearduino.py', '1')
            handlerObj.RecON = true
            handlerObj.Rec.stdout.on('data', async (data) => {
                console.log("Recording has started")
            });

            handlerObj.Rec.on('close', () =>{
                console.log("Recording terminated")
                handlerObj.Rec.kill()
                handlerObj.Rec = null
                handlerObj.intervalId = null
                handlerObj.startTime = 0
                _ = spawn('python3', '../../scripts/sendearduino.py', '0')
            });

            return "Recording started"
        }

        return "Recording already started"
    }

    console.log("handlerObj is null")
    return "Error starting stream"
}