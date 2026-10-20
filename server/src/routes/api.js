const express = require('express');
const router = express.Router();

const authCtrl = require('../controllers/authController');
const resCtrl = require('../controllers/resourceController');
const bookCtrl = require('../controllers/bookingController');
const adminCtrl = require('../controllers/adminController');

// Auth routes
router.post('/auth/login', authCtrl.login);
router.post('/auth/logout', authCtrl.logout);

// Resource routes
router.get('/resources', resCtrl.getResources);
router.get('/resources/:id', resCtrl.getResourceById);
router.post('/resources', resCtrl.addResource);
router.put('/resources/:id', resCtrl.updateResource);
router.delete('/resources/:id', resCtrl.deleteResource);

// Timeslot routes
router.get('/timeslots', resCtrl.getTimeSlots);

// Booking routes
router.post('/bookings', bookCtrl.createBooking);
router.get('/bookings', bookCtrl.getAllBookings);
router.delete('/bookings/:id', bookCtrl.cancelBooking);
router.post('/bookings/:id/checkin', bookCtrl.checkInBooking);

// Stats
router.get('/stats/utilization', bookCtrl.getUtilizationStats);

// Admin routes
router.get('/admin/violators', adminCtrl.getViolators);
router.post('/admin/users/:id/unlock', adminCtrl.unlockUser);
router.post('/admin/users/:id/lock', adminCtrl.lockUser);
router.get('/admin/audit-logs', adminCtrl.getAuditLogs);

module.exports = router;
