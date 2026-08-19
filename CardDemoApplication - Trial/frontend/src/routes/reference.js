const express = require('express');
const router  = express.Router();
const { requireRole } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

router.use(requireRole('ADMIN'));

// GET /admin/transaction-types — list
router.get('/', async (req, res) => {
    const { page = 1, typeCode = '', description = '' } = req.query;
    try {
        const client = apiClient(req);
        const response = await client.get('/reference/transaction-types', { params: { page, pageSize: 20, typeCode, description } });
        res.render('pages/reference/list.njk', { title: 'Transaction Types', data: response.data.data, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Error', message: 'Could not load types.', statusCode: 500 });
    }
});

// GET /admin/transaction-types/add
router.get('/add', (req, res) => {
    res.render('pages/reference/form.njk', { title: 'Add Transaction Type', mode: 'add', formData: {}, errors: null });
});

// POST /admin/transaction-types/add
router.post('/add', async (req, res) => {
    const { typeCode, description } = req.body;
    try {
        const client = apiClient(req);
        await client.post('/reference/transaction-types', { typeCode, description });
        req.session.flash = { success: `Transaction type ${typeCode} created.` };
        res.redirect('/admin/transaction-types');
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Failed to create type.';
        res.render('pages/reference/form.njk', { title: 'Add Transaction Type', mode: 'add', formData: req.body, errors: [{ text: msg }] });
    }
});

// GET /admin/transaction-types/:typeCode/edit
router.get('/:typeCode/edit', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/reference/transaction-types/${req.params.typeCode}`);
        res.render('pages/reference/form.njk', { title: 'Edit Transaction Type', mode: 'edit', formData: response.data.data, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'Type not found.', statusCode: 404 });
    }
});

// POST /admin/transaction-types/:typeCode/edit
router.post('/:typeCode/edit', async (req, res) => {
    const { description, rowVersion } = req.body;
    try {
        const client = apiClient(req);
        await client.put(`/reference/transaction-types/${req.params.typeCode}`, { description, rowVersion: parseInt(rowVersion, 10) });
        req.session.flash = { success: 'Transaction type updated.' };
        res.redirect('/admin/transaction-types');
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Update failed.';
        res.render('pages/reference/form.njk', { title: 'Edit Transaction Type', mode: 'edit', formData: req.body, errors: [{ text: msg }] });
    }
});

// GET /admin/transaction-types/:typeCode/delete — confirmation page
router.get('/:typeCode/delete', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/reference/transaction-types/${req.params.typeCode}`);
        res.render('pages/reference/delete.njk', { title: 'Delete Transaction Type', type: response.data.data });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'Type not found.', statusCode: 404 });
    }
});

// POST /admin/transaction-types/:typeCode/delete
router.post('/:typeCode/delete', async (req, res) => {
    try {
        const client = apiClient(req);
        await client.delete(`/reference/transaction-types/${req.params.typeCode}`);
        req.session.flash = { success: `Transaction type ${req.params.typeCode} deleted.` };
        res.redirect('/admin/transaction-types');
    } catch (err) {
        req.session.flash = { error: 'Failed to delete type.' };
        res.redirect('/admin/transaction-types');
    }
});

module.exports = router;
