const os = require('os');
const path = require('path');
//const bodyParser = require('body-parser');


var handler = require('../controllers/handler_rec')
const pythonProcess = spawn('python3', ['../../scripts/receivearduino.py']);
var handlerObj = null;

pythonProcess.stdout.on('data', (data) =>{
    const output = data.toString().partition('!')[0]

    if (handlerObj != null) {
        if (output === 'sensor') {
            handlerObj.startTime = Date.now()
            handler.initRec(handlerObj)
        } else if (output === 'ring') {
            handler.initRec(handlerObj)
        }
    }
});

const checktime = setInterval((handlerObj) => {
    console.log("RecordingHandlerObj: "+ JSON.stringify(handlerObj))
    var elapsed_time = Math.floor(Date.now() - handlerObj.startTime)
    console.log("Elapsed time: "+ elapsed_time)
    if (elapsed_time > 5) {
        handlerObj.Rec.kill()
        handlerObj.Rec = null
        handlerObj.startTime = 0
    }
})

module.exports = {
    recordingHandler,
    setHandlerObj: function(inithandlerObj) {
        handlerObj = inithandlerObj;
    }
}                