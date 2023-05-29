const fs = require('fs');
const path = require('path');
const express = require('express');
const { ObjectId } = require('mongodb');
const bodyParser = require('body-parser');
const token = require('../controllers/auth');

var router = express.Router()

var RECORDINGS_FOLDER = "../multimedia/"
const collectionName = 'videos';

var db = null;

const refreshDirectory = async (db) => {
    try {
        files = fs.readdirSync(path.resolve(RECORDINGS_FOLDER));
        console.log("files in dir: ", files)
        for (var index in files) {
            const existingFile = await db.collection('videos').findOne({ path: files[index] });
            console.log("file name: "+ files[index])
            if (existingFile) {
              console.log(`File ${files[index]} already exists in the database. Skipping...`);
              continue;
            }
            db.collection(collectionName).insertOne({"path": files[index], "datetime": new Date()})
        }

        console.log('All files added to the database');
        
    } catch (err) {
        console.error('Failed to read the directory:', err);
    }
}

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

    try {
        const refreshVideos = await refreshDirectory(db);
        const videos = await db.collection(collectionName).find().toArray();
        const formattedVideos = videos.map(video => ({
          "id": video.id,
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

    const videoId = req.params.videoId;
    console.log("file id:"+ JSON.stringify(req.query))
    try {
        const result = await db.collection('videos').deleteOne({ id: new ObjectId(videoId) });

        if (result.deletedCount === 1) {
            const videos = await db.collection(collectionName).find().toArray();
            const formattedVideos = videos.map(video => ({
                "id": video.id,
                "path": video.path,
                "datetime": video.datetime
            }));
            console.log("formated: "+formattedVideos)
            const videoData = formattedVideos.find(video => video.id === videoId)
            if (!videoData) {
                console.log("Error deleting file with id:" + videoId)
            } else {
                fs.unlink(path.resolve(RECORDINGS_FOLDER+videoData.path), (err) => {
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
        res.status(500).json({ message: 'Internal server error' });
    }
});
    
router.get('/:filename', (req, res) => {
    const filename = req.params.filename;
    //console.log("recordings folder: "+path.resolve(RECORDINGS_FOLDER))

    fs.readdir(path.resolve(RECORDINGS_FOLDER), (err, files) => {
        if (err) {
            console.log("Error reading files:" + err)
            res.status(500).json({ message: 'Internal server error' })
        } else {
            files.forEach((file) => {

                //filename is saved in the db without the extension(.mp4,.h264...) because of url purposes
                if (file.includes(filename)) {
                  const videoPath = path.resolve(RECORDINGS_FOLDER + file);
                  console.log("path to file: " + videoPath)
                  // Serve the video file
                res.sendFile(path.resolve(videoPath));
                }
            });
        }
    });
});

module.exports = {
    router,
    refreshDirectory,
    setDB: function(database) {
        db = database;
    }
}