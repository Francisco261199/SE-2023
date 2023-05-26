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
        return res.status(500).json({ status: 500, message: 'Database connection not established' });
    }
    // Exemplo de inserção:
    // db.collection(collectionName).insertOne({"path": "example.mp4", "datetime": "2013-01-01T00:00:00"})

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
    const videoPath = __dirname + '/../recordings/' + filename; // modificar o caminho caso seja preciso
    
    fs.readdir(RECORDINGS_FOLDER, (err, files) => {
      if (err) {
        console.error('Error reading folder:', err);
        return;
      }
      var files = []
      files.forEach((file) => {
        if (file == nil) {
          return
        }
          const filePath = path.join(RECORDINGS_FOLDER, file);
    
        console.log(filePath);
      });
  });
    
    // Serve the video file
    res.sendFile(path.resolve(videoPath));
});

module.exports = {
    router,
    setDB: function(database) {
        db = database;
    }
}