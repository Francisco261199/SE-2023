const { spawn } = require('child_process')

module.exports.initRec = function (handlerObj){
    if(handlerObj != null) {
        if( !handlerObj.RecON ) {
            console.log("Init Recording")
            handlerObj.Rec = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/record.py'])
            _ = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/sendarduino.py', '1'])
            handlerObj.RecON = true
            handlerObj.startTime = Date.now()
            handlerObj.Rec.stdout.on('data', async (data) => {
                console.log("Recording has started")
            });

            handlerObj.Rec.on('close', () =>{
                console.log("Recording terminated")
                //handlerObj.Rec.kill()
                //handlerObj.Rec = null
                
                //handlerObj.startTime = 0
                clearInterval(handlerObj.intervalId)
                handlerObj.intervalId = null
                _ = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/sendarduino.py', '0'])
            });

            return "Recording started"
        }

        return "Recording already started"
    }

    console.log("handlerObj is null")
    return "Error starting stream"
}