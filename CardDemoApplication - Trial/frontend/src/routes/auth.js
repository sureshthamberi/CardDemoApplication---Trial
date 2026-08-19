const express = require('express');
const router  = express.Router();
const { apiClient } = require('../config/apiClient');
const { requireAuth } = require('../middleware/auth');

// GET /auth/login
router.get('/login', (req, res) => {
    if (req.session && req.session.user) {
        return res.redirect('/menu');
    }
    res.render('pages/auth/login.njk', { title: 'Sign In — CardDemo', errors: null });
});

// POST /auth/login
router.post('/login', async (req, res) => {
    const { userId, password } = req.body;
    const errors = [];

    if (!userId || userId.trim() === '') errors.push({ text: 'User ID is required', href: '#userId' });
    if (!password || password.trim() === '') errors.push({ text: 'Password is required', href: '#password' });

    if (errors.length) {
        return res.render('pages/auth/login.njk', { title: 'Sign In — CardDemo', errors, userId });
    }

    try {
        const client = apiClient(null);
        const response = await client.post('/auth/login', { userId: userId.trim(), password: password.trim() });
        const data = response.data.data;

        req.session.user  = { userId: data.userId, displayName: data.displayName, userType: data.userType };
        req.session.token = data.token;

        if (data.userType === 'ADMIN') {
            return res.redirect('/menu/admin');
        }
        return res.redirect('/menu/main');
    } catch (err) {
        const errMsg = err.response && err.response.data
            ? (err.response.data.message || 'Invalid credentials')
            : 'Service unavailable. Please try again.';
        res.render('pages/auth/login.njk', { title: 'Sign In — CardDemo', errors: [{ text: errMsg }], userId });
    }
});

// POST /auth/logout
router.post('/logout', requireAuth, async (req, res) => {
    try {
        const client = apiClient(req);
        await client.post('/auth/logout');
    } catch (e) { /* best effort */ }
    req.session.destroy();
    res.redirect('/auth/login');
});

module.exports = router;
