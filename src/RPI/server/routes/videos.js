const fs = require('fs');
const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');
const token = require('../controllers/auth')

var router = express.Router()

var RECORDING_FOLDER = "~/camera/multimedia/"

router.get('/', (req, res) => {
    response = token.authenticateToken(req,res)
    if (response !== "User validated") {
        console.log(response)
        return res.status(400).json({ message: response })
    }

    fs.readdir(RECORDING_FOLDER, (err, files) => {
        if (err) {
          console.error('Error reading folder:', err);
          return;
        }
        var files = []
        files.forEach((file) => {
          if (file == nil) {
            return
          }
            const filePath = path.join(RECORDING_FOLDER, file);
      
          console.log(filePath);
        });
    });

    res.status(200).json({
        "id": "test",
        "url": "test",
        "dateTime": new Date(),
    })
});
    
router.get('/:videoId', (req, res) => {
    const videoId = req.params.videoId;
    const format = req.query.ext;
    const videoData = {
        id: videoId,
        datetime: '2023-05-15',
        message: `Video ${videoId} in ${format} format`,
    };
    res.json(videoData);
});

module.exports = router;