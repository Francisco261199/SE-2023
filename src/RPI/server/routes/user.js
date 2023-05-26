const express = require('express');
const bodyParser = require('body-parser');
const token = require('../controllers/auth')
const bcrypt = require('bcrypt');
var router = express.Router()

const collectionName = 'users';

var db = null;

router.post('/new', (req, res) => {
    const { username , email, password } = req.body;
    console.log('New request received for user:', username, email, password);
    
    if (!username || !email || !password) {
      return res.status(400).json({ message: 'Missing parameters for user creation' });
    }
    
    if (!db) {
      return res.status(500).json({ message: 'Database connection not established' });
    }

    db.collection(collectionName).findOne({ $or: [{ username }, { email }] })
    .then(existingUser => {
      if (existingUser) {
        console.log("User already exists")
        return res.status(409).json({ message: 'User already exists' });
      }

      bcrypt.hash(password, 10, (err, hashedPassword) => {
        if (err) {
          console.error('Error hashing password:', err);
          return res.status(500).json({ error: 'Internal server error' });
        }

        // Create the user object with hashed password
        const newUser = {
          username,
          email,
          password: hashedPassword,
        };
      
        //Save user
        db.collection(collectionName).insertOne(newUser)
          .then(() => {
            return res.status(200).json({ message: "User successfully created" });
          })
          .catch(err => {
            console.error('Failed to create user:', err);
            return res.status(500).json({ message: 'Failed to create user' });
        });
      });
    })
    .catch(err => {
      console.error('Error finding user: ', err);
      return res.status(500).json({ message: 'Internal server error' });
    });
});
  
router.post('/login', (req, res) => {
    const { username, password } = req.body;
    console.log('Login request received for user:', username, password);
    
    if (!username || !password) {
      console.log('Missing parameters for user creation')
      return res.status(400).json({ message: 'Missing parameters for user creation' });
    }

    if (!db) {
      console.log('Database connection not established')
      return res.status(500).json({ message: 'Internal server error' });
    }

    db.collection(collectionName).findOne({ username })
    .then(user => {
      bcrypt.compare(password, user.password, (err, match) => {
        if (err) {
          console.error('Error comparing passwords:', err);
          return res.status(500).json({ error: 'Internal server error' });
        }

        if (!user || !match){
          console.log('Invalid credentials')
          return res.status(401).json({ message: 'Invalid credentials' });
        }

        return res.json({ "token": token.generateAccessToken(user) });
      });
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