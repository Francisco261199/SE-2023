const { spawn } = require('child_process')

module.exports.initRec = function (handlerObj){
    if(handlerObj != null) {
        if( !handlerObj.RecON ) {
            console.log("Init Recording")
            handlerObj.Rec = spawn('python3', ['../../scripts/recordings.py'])
            handlerObj.RecON = true
            handlerObj.Rec.stdout.on('data', async (data) => {
                console.log("Recording has started at port:" + port)
                
                if (data === 'ring') {
                    //notify
                } else if (data === 'sensor') {
                    handlerObj.startTime = Date.now()
                    //notify
                }
            });

            handlerObj.Rec.on('close', () =>{
                console.log("Recording terminated")
                handlerObj.Rec.kill()
                handlerObj.Rec = null
                handlerObj.startTime = 0
            });

            return "Recording started"
        }

        return "Recording already started"
    }

    console.log("handlerObj is null")
    return "Error starting stream"
}

// module.exports.stopRec = function (handlerObj){
//     if(handlerObj != null) {
//         if( handlerObj.streamON && handlerObj.nviewers == 1 ) {
//             handlerObj.stream.kill()
//             handlerObj.stream = null
//             handlerObj.streamON = false
//             handlerObj.nviewers = 0
//             console.log("Stream stopped")
//             return "Stream stopped"

//         } else if ( handlerObj.streamON && handlerObj.nviewers > 1 ) {
//             handlerObj.nviewers--
//             console.log("Viewer count: "+ handlerObj.nviewers)
//             return "Stream stopped for user"
//         }
//     }
//     console.log("handlerObj is null")
//     return "Error stopping stream"
// }