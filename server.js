const express = require('express');
const mysql = require('mysql2');
const bodyParser = require('body-parser');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
const port = 8080;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('static')); // Serve static files

// Start server
app.listen(port, () => {
  console.log(`🚀 Server is running at http://localhost:${port}`);
});

// MySQL connection
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: 'Bhuvana@4000',
  database: 'onlineexam'
});

db.connect(err => {
  if (err) {
    console.error('❌ MySQL connection failed:', err);
    return;
  }
  console.log('✅ Connected to MySQL');
});

// Root route
app.get('/', (req, res) => {
  res.send('✅ Server is running!');
});

// LOGIN route
app.post('/ONLINE_EXAM/login', (req, res) => {
  const { loginId, password } = req.body;

  console.log('🚨 Login Attempt');
  console.log('📥 Entered loginId:', loginId);
  console.log('📥 Entered password:', password);

  if (!loginId || !password) {
    return res.status(400).json({ message: 'Please enter login ID and password' });
  }

  db.query('SELECT * FROM logins WHERE login_id = ?', [loginId], (err, results) => {
    if (err) {
      console.error('❌ SQL Error:', err);
      return res.status(500).json({ message: 'Database error' });
    }

    console.log('🔍 DB Result:', results);

    if (results.length === 0) {
      return res.status(400).json({ message: 'Invalid login ID' });
    }

    const user = results[0];

    if (user.password !== password) {
      console.log('❌ Password mismatch');
      return res.status(400).json({ message: 'Invalid password' });
    }

    const token = jwt.sign({ userId: user.student_id }, 'secret', { expiresIn: '1h' });
    console.log('✅ Login success, token sent');
    res.json({ message: 'Login successful', token });
  });
});

app.post('/ONLINE_EXAM/teacherlogin', (req, res) => {
  const { loginId, password } = req.body;

  if (!loginId || !password) {
    return res.status(400).json({ message: 'Please enter login ID and password' });
  }

  db.query('SELECT * FROM teacher_logins WHERE login_id = ?', [loginId], (err, results) => {
    if (err) {
      console.error('❌ SQL Error:', err);
      return res.status(500).json({ message: 'Database error' });
    }

    if (results.length === 0) {
      return res.status(400).json({ message: 'Invalid login ID' });
    }

    const user = results[0];
    if (user.password !== password) {
      return res.status(400).json({ message: 'Invalid password' });
    }

    const token = jwt.sign({ teacherId: user.teacher_id }, 'secret', { expiresIn: '1h' });
    res.json({ message: 'Login successful', token });
  });
});