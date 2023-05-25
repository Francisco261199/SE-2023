const express = require('express');
const bodyParser = require('body-parser');
const token = require('../controllers/auth')
var router = express.Router()

const collectionName = 'users';

var db = null;

router.post('/new', (req, res) => {
    const { name ,email, password } = req.body;
    
    if (!name || !email || !password) {
      return res.status(400).json({ message: 'Missing parameters for user creation' });
    }
    
    if (!db) {
      return res.status(500).json({ message: 'Database connection not established' });
    }

    db.collection(collectionName).findOne({ email })
    .then(existingUser => {
      if (existingUser) {
        console.log("Database not running")
        return res.status(409).json({ message: 'User already exists' });
      }
      const newUser = { name, email, password };

      //Save user
      db.collection(collectionName).insertOne(newUser)
        .then(() => {
          // Generate an access token
          res.json({ message: "User successfully created" });
        })
        .catch(err => {
          console.error('Failed to create user:', err);
          res.status(500).json({ message: 'Failed to create user' });
        });
    })
    .catch(err => {
      console.error('Failed to check existing user:', err);
      res.status(500).json({ message: 'Failed to create user' });
    });
});
  
router.post('/login', (req, res) => {
    const { name, password } = req.body;
    
    if (!name || !password) {
      return res.status(400).json({ message: 'Missing parameters for user creation' });
    }

    if (!db) {
      return res.status(400).json({ status: 500, message: 'Database connection not established' });
    }

    db.collection(collectionName).findOne({ name })
    .then(user => {
      if (!user || user.password !== password) {
        return res.status(401).json({ message: 'Invalid credentials' });
      }
      res.json({ "token": token.generateAccessToken(user) });
    })
    .catch(err => {
      console.error('Failed to find user:', err);
      res.status(500).json({ message: 'Failed to login' });
    });
});

module.exports = {
  router,
  setDB: function(database) {
    db = database;
  }
}