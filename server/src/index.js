const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const path = require('path');
require('dotenv').config();

const db = require('./config/db');
const apiRoutes = require('./routes/api');

const app = express();
const PORT = process.env.PORT || 8088;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(morgan('dev'));

// Static Admin Web Portal
const adminWebPath = path.join(__dirname, '../../admin_web');
app.use('/admin', express.static(adminWebPath));

// API Routes
app.use('/api', apiRoutes);

// Health check endpoint
app.get('/health', async (req, res) => {
    try {
        const dbCheck = await db.query('SELECT NOW() as now, current_database() as db;');
        res.status(200).json({
            status: 'UP',
            service: 'F-Space Enterprise Backend API',
            database: dbCheck.rows[0].db,
            dbTime: dbCheck.rows[0].now,
            timestamp: new Date().toISOString()
        });
    } catch (err) {
        res.status(500).json({ status: 'DOWN', error: err.message });
    }
});

// Root welcome
app.get('/', (req, res) => {
    res.json({
        message: 'F-Space Workspace & Meeting Room Booking Management API Server',
        version: '1.0.0',
        adminPortal: 'http://localhost:' + PORT + '/admin',
        endpoints: {
            auth: '/api/auth/login',
            resources: '/api/resources',
            timeslots: '/api/timeslots',
            bookings: '/api/bookings',
            stats: '/api/stats/utilization',
            admin: '/api/admin/violators'
        }
    });
});

// Global error handler
app.use((err, req, res, next) => {
    console.error('Unhandled server error:', err);
    res.status(500).json({ message: 'Internal Server Error: ' + err.message });
});

// Start listening
app.listen(PORT, '0.0.0.0', () => {
    console.log(`=================================================================`);
    console.log(`🚀 F-Space Backend Server running on http://0.0.0.0:${PORT}`);
    console.log(`📡 API Endpoints Base: http://localhost:${PORT}/api/`);
    console.log(`🌐 Admin Web Portal: http://localhost:${PORT}/admin/`);
    console.log(`📦 Database: PostgreSQL (fspace_db) via Docker`);
    console.log(`=================================================================`);
});
