const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

// GET /menu/main
router.get('/main', requireAuth, async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get('/navigation/main-menu');
        const menu = response.data.data;
        res.render('pages/menu/main.njk', { title: 'Main Menu — CardDemo', menu });
    } catch (err) {
        res.render('error.njk', { title: 'Menu error', message: 'Could not load main menu.', statusCode: 500 });
    }
});

// GET /menu/admin
router.get('/admin', requireAuth, async (req, res) => {
    if (req.session.user.userType !== 'ADMIN') {
        return res.redirect('/menu/main');
    }
    try {
        const client = apiClient(req);
        const response = await client.get('/navigation/admin-menu');
        const menu = response.data.data;
        res.render('pages/menu/admin.njk', { title: 'Admin Menu — CardDemo', menu });
    } catch (err) {
        res.render('error.njk', { title: 'Menu error', message: 'Could not load admin menu.', statusCode: 500 });
    }
});

// Redirect /menu to role-based menu
router.get('/', requireAuth, (req, res) => {
    if (req.session.user.userType === 'ADMIN') return res.redirect('/menu/admin');
    return res.redirect('/menu/main');
});

module.exports = router;
