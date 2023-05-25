const { spawn } = require('child_process')
var nviewers = 0

module.exports.initStream = function (handlerObj){
    if(handlerObj != null) {
        if( !handlerObj.streamON ) {
            console.log("Init Stream")
            handlerObj.stream = spawn('python3', ['./test.py'])
            handlerObj.streamON = true

            handlerObj.stream.stdout.once('data', async () => {
            })

            handlerObj.stream.on('close', () =>{
                console.log("stream terminated")
            })
            nviewers++
        } else if ( handlerObj.streamON ) {
            nviewers++
        }

    }
}
module.exports.stopStream = function (handlerObj){
    if(handlerObj != null) {
        if( handlerObj.streamON && nviewers == 1 ) {
            console.log("Stop Stream")
            handlerObj.stream.kill()
            handlerObj.streamON = false
            nviewers = 0
        } else if ( handlerObj.streamON && nviewers >= 1 ) {
            nviewers--
        }
    }
}