const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');
const { randomUUID } = require('crypto');

router.use(requireAuth);

// GET /payments/bill — bill payment form
// accountId may arrive as a query param from Account Inquiry action links
router.get('/bill', (req, res) => {
    const { accountId } = req.query;
    res.render('pages/payments/bill.njk', { title: 'Bill Payment', preview: null, errors: null, accountId: accountId || '' });
});

// POST /payments/bill/preview — preview payment
router.post('/bill/preview', async (req, res) => {
    const { accountId } = req.body;
    try {
        const client = apiClient(req);
        const response = await client.get(`/payments/bill-payments/preview/${accountId}`);
        res.render('pages/payments/bill.njk', {
            title: 'Bill Payment',
            preview: response.data.data,
            accountId,
            errors: null
        });
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Preview failed.';
        res.render('pages/payments/bill.njk', { title: 'Bill Payment', preview: null, errors: [{ text: msg }], accountId });
    }
});

// POST /payments/bill/confirm — execute payment
router.post('/bill/confirm', async (req, res) => {
    const { accountId, confirmation } = req.body;
    try {
        const client = apiClient(req);
        const idempotencyKey = require('crypto').randomUUID();
        const response = await client.post('/payments/bill-payments',
            { accountId, confirmation },
            { headers: { 'X-Idempotency-Key': idempotencyKey } }
        );
        res.render('pages/payments/success.njk', {
            title: 'Payment Successful',
            result: response.data.data
        });
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Payment failed.';
        res.render('pages/payments/bill.njk', { title: 'Bill Payment', preview: { accountId }, errors: [{ text: msg }] });
    }
});

module.exports = router;
