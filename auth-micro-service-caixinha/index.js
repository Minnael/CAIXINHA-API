const express = require('express');
const cors = require('cors');
require('dotenv').config();
const connectDB = require('./config/db');
const authRoutes = require('./routes/auth');

connectDB();

const app = express();
app.use(cors());
app.use(express.json());
app.use('/api', authRoutes);

// Health check endpoint para Docker
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'OK', service: 'auth-service' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Auth service rodando na porta ${PORT}`));