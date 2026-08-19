const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

router.use(requireAuth);

// GET /cards/search — card search form
router.get('/search', async (req, res) => {
    const { accountId, cardNumber, page = 1 } = req.query;
    if (!accountId && !cardNumber) {
        return res.render('pages/cards/search.njk', { title: 'Card Search', data: null, errors: null });
    }
    try {
        const client = apiClient(req);
        const response = await client.get('/cards', { params: { accountId, cardNumber, page, pageSize: 20 } });
        res.render('pages/cards/search.njk', { title: 'Card Search', data: response.data.data, errors: null, query: req.query });
    } catch (err) {
        res.render('pages/cards/search.njk', { title: 'Card Search', data: null, errors: [{ text: 'Search failed.' }] });
    }
});

// GET /cards/detail — card detail form
router.get('/detail', async (req, res) => {
    const { cardNumber } = req.query;
    if (!cardNumber) return res.render('pages/cards/detail.njk', { title: 'Card Detail', card: null, errors: null });
    try {
        const client = apiClient(req);
        const response = await client.get(`/cards/${cardNumber}`);
        res.render('pages/cards/detail.njk', { title: 'Card Detail', card: response.data.data, errors: null });
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Card not found.';
        res.render('pages/cards/detail.njk', { title: 'Card Detail', card: null, errors: [{ text: msg }] });
    }
});

// GET /cards/:cardNumber/edit — card update form
router.get('/:cardNumber/edit', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/cards/${req.params.cardNumber}`);
        res.render('pages/cards/edit.njk', { title: 'Update Card', card: response.data.data, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'Card not found.', statusCode: 404 });
    }
});

// POST /cards/:cardNumber/edit — submit card update
router.post('/:cardNumber/edit', async (req, res) => {
    const { cardNumber } = req.params;
    const { accountId, cardName, activeStatus, rowVersion } = req.body;
    // govukDateInput sends expiry-month / expiry-year (namePrefix "expiry")
    const expiryMonth = req.body['expiry-month'];
    const expiryYear  = req.body['expiry-year'];
    try {
        const client = apiClient(req);
        await client.put(`/cards/${cardNumber}`, {
            accountId,
            cardName,
            activeStatus,
            expiryMonth: parseInt(expiryMonth, 10),
            expiryYear: parseInt(expiryYear, 10),
            rowVersion: parseInt(rowVersion, 10)
        });
        req.session.flash = { success: 'Card updated successfully.' };
        res.redirect('/cards/search');
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Update failed.';
        res.render('pages/cards/edit.njk', { title: 'Update Card', card: req.body, errors: [{ text: msg }] });
    }
});

module.exports = router;
