const express = require('express');
const router  = express.Router();
const { requireRole } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

// Apply admin role to all routes
router.use(requireRole('ADMIN'));

// GET /admin/users — list users
router.get('/', async (req, res) => {
    const { page = 1, pageSize = 20, startUserId = '' } = req.query;
    try {
        const client = apiClient(req);
        const response = await client.get('/admin/users', { params: { page, pageSize, startUserId } });
        const data = response.data.data;
        res.render('pages/users/list.njk', { title: 'User Administration', data, page, pageSize });
    } catch (err) {
        res.render('error.njk', { title: 'Error', message: 'Could not load users.', statusCode: 500 });
    }
});

// GET /admin/users/add — add user form
router.get('/add', (req, res) => {
    res.render('pages/users/add.njk', { title: 'Add User', errors: null, formData: {} });
});

// POST /admin/users/add — submit add user
router.post('/add', async (req, res) => {
    const { firstName, lastName, userId, password, userType } = req.body;
    try {
        const client = apiClient(req);
        await client.post('/admin/users', { firstName, lastName, userId, password, userType });
        req.session.flash = { success: `User ${userId} created successfully.` };
        res.redirect('/admin/users');
    } catch (err) {
        const apiErrors = err.response && err.response.data ? buildErrors(err.response.data) : [{ text: 'Failed to create user.' }];
        res.render('pages/users/add.njk', { title: 'Add User', errors: apiErrors, formData: req.body });
    }
});

// GET /admin/users/:userId/edit — edit user form
router.get('/:userId/edit', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/admin/users/${req.params.userId}`);
        const user = response.data.data;
        res.render('pages/users/edit.njk', { title: 'Update User', user, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'User not found.', statusCode: 404 });
    }
});

// POST /admin/users/:userId/edit — submit update
router.post('/:userId/edit', async (req, res) => {
    const { firstName, lastName, password, userType, rowVersion } = req.body;
    try {
        const client = apiClient(req);
        await client.put(`/admin/users/${req.params.userId}`, {
            firstName, lastName, password, userType, rowVersion: parseInt(rowVersion, 10)
        });
        req.session.flash = { success: `User ${req.params.userId} updated successfully.` };
        res.redirect('/admin/users');
    } catch (err) {
        const apiErrors = err.response && err.response.data ? buildErrors(err.response.data) : [{ text: 'Failed to update user.' }];
        const user = { userId: req.params.userId, ...req.body };
        res.render('pages/users/edit.njk', { title: 'Update User', user, errors: apiErrors });
    }
});

// GET /admin/users/:userId/delete — confirm delete
router.get('/:userId/delete', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/admin/users/${req.params.userId}`);
        const user = response.data.data;
        res.render('pages/users/delete.njk', { title: 'Delete User', user, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'User not found.', statusCode: 404 });
    }
});

// POST /admin/users/:userId/delete — perform delete
router.post('/:userId/delete', async (req, res) => {
    try {
        const client = apiClient(req);
        await client.delete(`/admin/users/${req.params.userId}`);
        req.session.flash = { success: `User ${req.params.userId} deleted.` };
        res.redirect('/admin/users');
    } catch (err) {
        res.render('error.njk', { title: 'Error', message: 'Failed to delete user.', statusCode: 500 });
    }
});

function buildErrors(apiData) {
    if (apiData.fieldErrors && apiData.fieldErrors.length) {
        return apiData.fieldErrors.map(fe => ({ text: fe.message, href: `#${fe.field}` }));
    }
    return [{ text: apiData.message || 'An error occurred.' }];
}

module.exports = router;
