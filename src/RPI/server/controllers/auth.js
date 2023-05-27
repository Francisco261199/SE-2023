const jwt = require('jsonwebtoken');

// Secret key for signing JWT
const secretKey = 'secret';

var token = {}

token.authenticateToken = (req, res) => {
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(' ')[1];
  
    if (!token) {
      console.log("token:"+req.headers.toString())
      return "Missing token"
    }
  
    return jwt.verify(token, secretKey, (err) => {
      if (err) {
        console.error('Invalid token');
        return "Invalid token"
      }
  
      return "User validated"
    });
}

token.generateAccessToken = user => {
    return jwt.sign(user, secretKey);
};


module.exports = token