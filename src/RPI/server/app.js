const createError = require('http-errors')
const express = require('express');
const bodyParser = require('body-parser');
const { MongoClient } = require('mongodb');

var videoRouter = require('./routes/videos').router
var streamRouter = require('./routes/stream').router
var userRouter = require('./routes/user').router
var userDB = require('./routes/user')
var videoDB = require('./routes/videos')
var streamObj = require('./routes/stream')

var app = express();

var initHandlerObj = {
  streamON: false,
  recON: false,
  stream: null,
  nviewers: 0
}
const port = 3000;
const intf = '0.0.0.0'


// MongoDB connection URI
const mongoURI = 'mongodb://127.0.0.1:27017';
const dbName = 'AppDB';

app.use(express.json());

// Function to connect to MongoDB
const connectToMongoDB = async () => {
  try {
    const client = await MongoClient.connect(mongoURI, { useUnifiedTopology: true });
    console.log('Connected to MongoDB');
    const db = client.db(dbName);
    // Pass the 'db' object to your route handlers or controllers as needed
    userDB.setDB(db);
    videoDB.setDB(db);
    streamObj.setHandlerObj(initHandlerObj)
  } catch (err) {
    console.error('Failed to connect to MongoDB:', err);
    process.exit(1); // Terminate the application if MongoDB connection fails
  }
};

// Connect to MongoDB and start the server
connectToMongoDB().then(() => {
  app.use('/', function(req, res, next){
    console.log("Got request type: "+req.method+" to path: "+req.url)
    next()
  })

  app.use('/user', userRouter)
  app.use('/videos', videoRouter)
  app.use('/stream', streamRouter)

  app.use('/isAlive', function(req, res, next){
    res.status(200).send("Running")
  })
  
  app.use(function(req, res, next){
    next(createError(404))
  })

  app.listen(port, intf, () => {
    console.log(`Server listening on port ${port}`);
  });
  
});
