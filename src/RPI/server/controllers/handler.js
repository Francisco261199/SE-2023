const { spawn } = require('child_process')

module.exports.initStream = function (handlerObj, port){
    if(handlerObj != null) {
        if( !handlerObj.streamON ) {
            console.log("Init Stream")
            handlerObj.stream = spawn('python3', ['../../scripts/stream.py', port])
            handlerObj.streamON = true

            handlerObj.stream.stdout.once('data', async () => {
                console.log("Stream has started at port:" + port)
            })

            // handlerObj.stream.on('close', () =>{
            //     console.log("Stream terminated")
            //     handlerObj.streamON = false
            //     handlerObj.stream.kill() 
            //     handlerObj.stream = null
            //     handlerObj.nviewers = 0
            // })

            handlerObj.nviewers++
            console.log("Viewer count: "+ handlerObj.nviewers)
            return "Stream started"

        } else if ( handlerObj.streamON ) {
            handlerObj.nviewers++
            console.log("Viewer count: "+ handlerObj.nviewers)
            return "Stream already running"
        }
    }

    console.log("handlerObj is null")
    return "Error starting stream"
}

module.exports.stopStream = function (handlerObj){
    if(handlerObj != null) {
        if( handlerObj.streamON && handlerObj.nviewers == 1 ) {
            handlerObj.stream.kill()
            handlerObj.stream = null
            handlerObj.streamON = false
            handlerObj.nviewers = 0
            console.log("Stream stopped")
            return "Stream stopped"

        } else if ( handlerObj.streamON && handlerObj.nviewers > 1 ) {
            handlerObj.nviewers--
            console.log("Viewer count: "+ handlerObj.nviewers)
            return "Stream stopped for user"
        }
    }
    console.log("handlerObj is null")
    return "Error stopping stream"
}