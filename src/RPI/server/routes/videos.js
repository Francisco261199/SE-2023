const fs = require('fs');
const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');
const token = require('../controllers/auth')

var router = express.Router()

var RECORDINGS_FOLDER = "~/camera/multimedia/"
const collectionName = 'videos';

var db = null;

router.get('/', async (req, res) => {
    response = token.authenticateToken(req,res)
    if (response !== "User validated") {
        console.log(response)
        return res.status(400).json({ message: response })
    }

    if (!db) {
        console.log('Database connection not established')
        return res.status(500).json({ status: 500, message: 'Internal server error' });
    }
    // Exemplo de inserção:
    // db.collection(collectionName).insertOne({"path": "example", "datetime": "2013-01-01T00:00:00"})

    try {
        const videos = await db.collection(collectionName).find().toArray();
        const formattedVideos = videos.map(video => ({
          "id": video._id,
          "path": video.path,
          "datetime": video.datetime
        }));
        res.json(formattedVideos);
    } catch (err) {
        console.error('Failed to fetch videos:', err);
        res.status(500).json({ message: 'Failed to fetch videos' });
    }
});
    
router.get('/:filename', (req, res) => {
    const filename = req.params.filename;
    
    fs.readdir(RECORDINGS_FOLDER, (err, files) => {
      if (err) {
        console.error('Error reading folder:', err);
        return res.status(500).json({ status: 500, message: 'Internal server error' });
      }

      files.forEach((file) => {
        if (file == nil) {
          console.log("No files exist in: " + RECORDINGS_FOLDER)
          return res.status(500).json({ status: 500, message: 'Internal server error' });
        }

        //filename is saved in the db without the extension(.mp4,.h264...) because of url purposes
        if (file.includes(filename)) {

          // Serve the video file
          return res.sendFile(path.resolve(videoPath));
        }
      });
    });

    return res.status(404).json({ message: "File not found" })
});

module.exports = {
    router,
    setDB: function(database) {
        db = database;
    }
}