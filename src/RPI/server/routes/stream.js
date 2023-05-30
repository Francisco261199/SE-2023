const os = require('os');
const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');


var router = express.Router()
var token = require('../controllers/auth');
var handler = require('../controllers/handler')
var handlerObj = {};

const DEFAULT_STREAM_PORT = "10001"
const DEFAULT_HOST_INTF = os.networkInterfaces()["wlan0"]

// Endpoint to stream camera feed
router.get('/', (req, res) => {
    // Set appropriate headers for image streaming
    res.writeHead(200, {
        'Content-Type': 'image/jpeg',
        'Connection': 'keep-alive',
        'Cache-Control': 'no-cache',
        'Transfer-Encoding': 'chunked'
    });

    // Create a new RaspiCam instance for the current request
    const camera = new RaspiCam({
        mode: 'photo',
        output: '-',
        encoding: 'jpg',
        width: 640,
        height: 480,
        quality: 75,
        timeout: 0
    });

    // Start capturing the camera feed for the current request
    camera.start();

    // When an image is captured, send it to the client
    camera.on('read', (data) => {
        res.write(data, 'binary');
    });

    // Handle any errors for the current request, if needed
    camera.on('error', (error) => {
        console.error(`Camera error: ${error}`);
    });

    // Handle the camera close event for the current request
    camera.on('stop', () => {
        res.end();
    });

    // Stop capturing the camera feed when the client disconnects
    res.on('close', () => {
        camera.stop();
    });
});

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
    setHandlerObj: function(inithandlerObj) {
        handlerObj = inithandlerObj;
    }
}