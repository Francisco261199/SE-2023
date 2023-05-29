const express = require('express');
const admin = require('firebase-admin');
require('dotenv').config({path: '../.env'})
var router = express.Router()

const collectionName = 'devices';

var db = null;

// Initialize Firebase Admin SDK
admin.initializeApp({
    credential: admin.credential.applicationDefault()
});

// Route to register a device
router.post('/registerDevice', async (req, res) => {
    if (!db) {
        console.error('Database connection not established');
        return res.status(500).json({ message: 'Internal server error' });
    }
    try {
        // Get the registration token from the request body
        const { token } = req.body;
        const devicesCollection = db.collection(collectionName);

        // Create a unique index on the registrationToken field
        await devicesCollection.createIndex({ token: 1 }, { unique: true });

        // Insert the registration token into the devices collection
        await devicesCollection.insertOne({ token });

        res.status(200).json({ message: 'Device registered successfully' });
    } catch (error) {
        if (error.code === 11000) {
            // Duplicate key error, token already exists
            return res.status(400).json({ message: 'Device already registered' });
        }

        console.error('Error registering device:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
});

// Notification TEST request
router.get('/notify', (req, res) => {
    // Send a general notification
    // Use sendNotification("Ding Ding Ding!", "Looks like someone rung your doorbell!") to send a notification
    sendNotification("Ding Ding Ding!", "Looks like someone rung your doorbell!")
        .then((response) => {
            console.log('Notification sent successfully:', response);
            res.status(200).json({ message: 'Notification sent successfully' });
        })
        .catch((error) => {
            console.error('Error sending notification:', error);
            res.status(500).json({ message: 'Error sending notification' });
        });
});

// Function to send a general notification to all devices
const sendNotification = async (title, body) => {
    if (!db) {
        console.log("Could connect to devices' database")
        return res.status(500).json({ message: 'Internal server error' });
    }
    try {
        const devicesCollection = db.collection(collectionName);

        // Get all devices from the collection
        const devices = await devicesCollection.find().toArray();
        const registrationTokens = devices.map((device) => device.token);
        //console.log('Devices:', registrationTokens);

        // Create notification message
        const message = {
            notification: {
                title,
                body,
            },
            tokens: registrationTokens,
        };

        // Send notification to all devices
        const response = await admin.messaging().sendMulticast(message);

        // Handle response as needed
        // console.log('Notification sent to all devices:', response);
    } catch (error) {
        console.error('Error sending notification to all devices:', error);
    }
};

module.exports = {
    router,
    sendNotification,
    setDB: function (database) {
        db = database;
    }
};
