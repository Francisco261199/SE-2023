const os = require('os');
const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');


var router = express.Router()
var token = require('../controllers/auth');
var handler = require('../controllers/handler_stream')
var handlerObj = null;
var recHandlerObj = null;

const DEFAULT_STREAM_PORT = "8554"
const DEFAULT_HOST_INTF = os.networkInterfaces()["wlan0"]

router.get('/start', async (req, res) => {
    
    response = token.authenticateToken(req,res)
    if (response !== "User validated") {
        console.log(response)
        return res.status(400).json({ message: response })
    }
    
    let IPv4Data = DEFAULT_HOST_INTF.find(entry => entry.family === 'IPv4')
    if (!IPv4Data) {
        console.log("Couldn't retrieve interface data")
        return res.status(500).json({ message: 'Internal server error' });
    }

    //Kill recording to prioritize live streaming
    if ( recHandlerObj.Rec != null ) {
        recHandlerObj.Rec.kill()
    }
    recHandlerObj.Rec = null
    recHandlerObj.startTime = 0
    clearInterval(recHandlerObj.intervalId)
    recHandlerObj.intervalId = null
    
    let resp = handler.initStream(handlerObj, DEFAULT_STREAM_PORT)
    if (resp === "Error starting stream") {
        return res.status(500).json({ message: 'Internal server error' });
    }

    res.json({
        "host": IPv4Data.address,
        "port": DEFAULT_STREAM_PORT,
    })

});

router.get('/stop', async (req, res) => {
    
    response = token.authenticateToken(req,res)
    if (response !== "User validated") {
        console.log(response)
        return res.status(400).json({ message: response })
    }

    let IPv4Data = DEFAULT_HOST_INTF.find(entry => entry.family === 'IPv4')
    if (!IPv4Data) {
        console.log("Couldn't retrieve interface data")
        return res.status(500).json({ message: 'Internal server error' });
    }
    
    let resp = handler.stopStream(handlerObj, DEFAULT_STREAM_PORT)
    if (resp === "Error stopping stream") {
        return res.status(500).json({ message: 'Internal server error' });
    }
    
    res.json({ message: "Stream stopped" })
});

module.exports = {
    router,
    setHandlerObj: function(initHandlerObj) {
        handlerObj = initHandlerObj;
    },
    setRecHandlerObj: function(initRecHandlerObj) {
        recHandlerObj = initRecHandlerObj;
    }
}