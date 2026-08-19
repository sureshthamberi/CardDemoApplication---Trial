const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

router.use(requireAuth);

// GET /pending-authorizations — enter account ID
router.get('/', (req, res) => {
    res.render('pages/pendingauth/search.njk', { title: 'Pending Authorizations', data: null, errors: null });
});

// POST /pending-authorizations — search by account
router.post('/', async (req, res) => {
    const { accountId } = req.body;
    try {
        const client = apiClient(req);
        const response = await client.get(`/pending-authorizations/accounts/${accountId}`);
        res.render('pages/pendingauth/list.njk', { title: 'Pending Authorizations', data: response.data.data, accountId, errors: null });
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Search failed.';
        res.render('pages/pendingauth/search.njk', { title: 'Pending Authorizations', data: null, errors: [{ text: msg }] });
    }
});

// GET /pending-authorizations/:authorizationId — detail
router.get('/:authorizationId', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/pending-authorizations/${req.params.authorizationId}`);
        res.render('pages/pendingauth/detail.njk', { title: 'Authorization Detail', auth: response.data.data, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'Authorization not found.', statusCode: 404 });
    }
});

// POST /pending-authorizations/:authorizationId/fraud/mark
router.post('/:authorizationId/fraud/mark', async (req, res) => {
    const { notes } = req.body;
    try {
        const client = apiClient(req);
        await client.post(`/fraud/authorizations/${req.params.authorizationId}/mark`,
            { notes },
            { headers: { 'X-Idempotency-Key': require('crypto').randomUUID() } }
        );
        req.session.flash = { success: 'Authorization marked as fraud.' };
        res.redirect(`/pending-authorizations/${req.params.authorizationId}`);
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Mark fraud failed.';
        res.redirect(`/pending-authorizations/${req.params.authorizationId}`);
    }
});

// POST /pending-authorizations/:authorizationId/fraud/unmark
router.post('/:authorizationId/fraud/unmark', async (req, res) => {
    const { notes } = req.body;
    try {
        const client = apiClient(req);
        await client.post(`/fraud/authorizations/${req.params.authorizationId}/unmark`,
            { notes },
            { headers: { 'X-Idempotency-Key': require('crypto').randomUUID() } }
        );
        req.session.flash = { success: 'Fraud flag removed.' };
        res.redirect(`/pending-authorizations/${req.params.authorizationId}`);
    } catch (err) {
        res.redirect(`/pending-authorizations/${req.params.authorizationId}`);
    }
});

module.exports = router;
