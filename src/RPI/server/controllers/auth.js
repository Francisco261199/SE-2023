const jwt = require('jsonwebtoken');

// Secret key for signing JWT
const secretKey = 'secret';

var token = {}

token.authenticateToken = (req, res) => {
    const authHeader = req.headers.authorization; //['Authorization'];
    const token = authHeader && authHeader.split(' ')[1];
  
    if (!token) {
      console.log("token:"+req.headers.toString())
      return res.status(401).json({ message: "Missing token" });
    }
  
    jwt.verify(token, secretKey, (err, user) => {
      if (err) {
        console.error('Invalid token:', err);
        return res.status(403).json({ message: "Invalid token" });
      }
  
      req.user = user;
      return token
    });
}

token.generateAccessToken = user => {
    return jwt.sign(user, secretKey);
};


module.exports = token