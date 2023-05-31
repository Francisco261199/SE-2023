const { spawn } = require('child_process')

module.exports.initRec = function (handlerObj){
    if(handlerObj != null) {
        if( !handlerObj.RecON ) {
            console.log("Init Recording")
            handlerObj.Rec = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/record.py'])
            _ = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/sendarduino.py', '1'])
            
            handlerObj.RecON = true
            handlerObj.startTime = Date.now()
            handlerObj.intervalId = setInterval(() => {
                console.log("startTime: "+ handlerObj.startTime)
                var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
                console.log("Elapsed time: "+ elapsed_time)
                if ( elapsed_time > (60 * 1000) ) { //stop after 1 minute
                    if (handlerObj.Rec != null) {
                        handlerObj.Rec.kill()
                    }
                    handlerObj.RecON = false
                    handlerObj.Rec = null
                    handlerObj.startTime = 0
                }
            }, 5000) //verify each 5 seconds if the recording is already 5 mins old
            handlerObj.Rec.stdout.on('data', async (data) => {
                console.log("Recording has started")
            });

            handlerObj.Rec.on('close', () =>{
                console.log("Recording terminated")
                //handlerObj.Rec.kill()
                handlerObj.Rec = null
                //db.collection(collectionName).insertOne({"path": files[index], "datetime": handlerObj.startTime})
                handlerObj.startTime = 0
                clearInterval(handlerObj.intervalId)
                handlerObj.intervalId = null
                _ = spawn('python3', ['/home/camera/camera/SE-2023/src/RPI/scripts/sendarduino.py', '0'])
                
            });

            return "Recording started"
        } else { //refresh interval to continue recording for the next 5 minutes
            clearInterval(handlerObj.intervalId)
            handlerObj.intervalId = setInterval(() => {
                console.log("startTime: "+ handlerObj.startTime)
                var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
                console.log("Elapsed time: "+ elapsed_time)
                if (elapsed_time > (60 * 1000) || handlerObj.Rec == null) {
                    if (handlerObj.Rec != null) {
                        handlerObj.Rec.kill()
                    }
                    handlerObj.RecON = false
                    handlerObj.Rec = null
                    handlerObj.startTime = 0
                }
            }, 5000)
        }

        return "Recording already started"
    }

    console.log("handlerObj is null")
    return "Error starting recording"
}