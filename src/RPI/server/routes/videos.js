const fs = require('fs');
const path = require('path');
const express = require('express');
const { ObjectId } = require('mongodb');
const bodyParser = require('body-parser');
const token = require('../controllers/auth');

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
    // db.collection(collectionName).insertOne({"path": RECORDINGS_FOLDER+"recording20230523_213717", "datetime": "20230524_112521"})

    try {
        const videos = await db.collection(collectionName).find().toArray();
        const formattedVideos = videos.map(video => ({
          "id": video._id,
          "path": video.path,
          "datetime": video.datetime
        }));
        console.log(JSON.stringify(formattedVideos))
        res.json(formattedVideos);
    } catch (err) {
        console.error('Failed to fetch videos:', err);
        res.status(500).json({ message: 'Internal server error' });
    }
});

router.get('/delete', async (req, res) => {
    response = token.authenticateToken(req, res)
    if (response !== "User validated") {
        console.log(response)
        return res.status(400).json({ message: response })
    }

    if (!db) {
        console.log('Database connection not established')
        return res.status(500).json({ status: 500, message: 'Internal server error' });
    }

    const videoId = req.query.videoId;

    try {

        const result = await db.collection('videos').deleteOne({ _id: new ObjectId(videoId) });

        if (result.deletedCount === 1) {
            
            const videos = await db.collection(collectionName).find().toArray();
            const formattedVideos = videos.map(video => ({
                "id": video._id,
                "path": video.path,
                "datetime": video.datetime
            }));

            const videoData = formattedVideos.find(video => video.id === videoId)
            if (!videoData) {
                console.log("Error deleting file with id:" + videoId)
            } else {
                fs.unlink(videoData.path, (err) => {
                    if (err) {
                        console.log("Error deleting file")
                    }
                });
            }

            res.json({ message: 'Video deleted successfully' });    
        } else {
            res.status(404).json({ message: 'Video not found' });
        }
    } catch (err) {
        console.error('Error deleting video:', err);
        res.status(500).json({ message: 'Failed to delete video' });
    }
});
    
router.get('/:filename', (req, res) => {
    const filename = req.params.filename;
    
    fs.readdir(RECORDINGS_FOLDER, (err, files) => {

      files.forEach((file) => {

        //filename is saved in the db without the extension(.mp4,.h264...) because of url purposes
        if (file.includes(filename)) {
          const videoPath = RECORDINGS_FOLDER + file;
          // Serve the video file
          res.sendFile(path.resolve(videoPath));
        }
      });
    });
});

module.exports = {
    router,
    setDB: function(database) {
        db = database;
    }
}