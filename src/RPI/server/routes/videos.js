const express = require('express');
const bodyParser = require('body-parser');
const token = require('../controllers/auth')
var router = express.Router()



router.get('/', token.authenticateToken, (req, res) => {
    res.json({
        "id": "test",
        "url": "test",
        "dateTime": new Date().toString
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