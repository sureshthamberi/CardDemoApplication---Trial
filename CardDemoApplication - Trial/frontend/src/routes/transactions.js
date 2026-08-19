const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

router.use(requireAuth);

// GET /transactions — list (optionally filtered by accountId)
router.get('/', async (req, res) => {
    const { page = 1, pageSize = 20, startTransactionId = '', accountId = '' } = req.query;
    try {
        const client = apiClient(req);
        const response = await client.get('/transactions', {
            params: { page, pageSize, startTransactionId, accountId }
        });
        res.render('pages/transactions/list.njk', {
            title: 'Transactions',
            data: response.data.data,
            errors: null,
            accountId   // pass through so the template can show it + pagination keeps the filter
        });
    } catch (err) {
        res.render('error.njk', { title: 'Error', message: 'Could not load transactions.', statusCode: 500 });
    }
});

// GET /transactions/add — add transaction form
router.get('/add', async (req, res) => {
    try {
        const client = apiClient(req);
        const typesResponse = await client.get('/reference/transaction-types', { params: { page: 1, pageSize: 100 } });
        const types = typesResponse.data.data.items;
        res.render('pages/transactions/add.njk', { title: 'Add Transaction', types, errors: null, formData: {} });
    } catch (err) {
        res.render('pages/transactions/add.njk', { title: 'Add Transaction', types: [], errors: null, formData: {} });
    }
});

// POST /transactions/add — submit add
router.post('/add', async (req, res) => {
    try {
        const client = apiClient(req);
        const idempotencyKey = require('crypto').randomUUID();
        const b = req.body;
        // Assemble ISO dates from govukDateInput day/month/year parts
        const origDay   = String(b['originalDate-day']   || '').padStart(2,'0');
        const origMonth = String(b['originalDate-month'] || '').padStart(2,'0');
        const origYear  = b['originalDate-year'] || '';
        const procDay   = String(b['processDate-day']    || '').padStart(2,'0');
        const procMonth = String(b['processDate-month']  || '').padStart(2,'0');
        const procYear  = b['processDate-year'] || '';
        const originalTimestamp = origYear ? `${origYear}-${origMonth}-${origDay}T00:00:00` : undefined;
        const processedTimestamp = procYear ? `${procYear}-${procMonth}-${procDay}T00:00:00` : undefined;
        const response = await client.post('/transactions',
            {
                accountId: b.accountId, cardNumber: b.cardNumber,
                transactionType: b.transactionType, categoryType: b.categoryType,
                source: b.source, amount: parseFloat(b.amount),
                description: b.description,
                originalTimestamp, processedTimestamp,
                merchantId: b.merchantId, merchantName: b.merchantName,
                merchantCity: b.merchantCity, merchantZip: b.merchantZip,
                confirmation: 'Y'
            },
            { headers: { 'X-Idempotency-Key': idempotencyKey } }
        );
        req.session.flash = { success: `Transaction ${response.data.data.transactionId} created.` };
        res.redirect('/transactions');
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Failed to add transaction.';
        res.render('pages/transactions/add.njk', { title: 'Add Transaction', types: [], errors: [{ text: msg }], formData: req.body });
    }
});

// GET /transactions/:id — transaction detail
router.get('/:transactionId', async (req, res) => {
    try {
        const client = apiClient(req);
        const response = await client.get(`/transactions/${req.params.transactionId}`);
        res.render('pages/transactions/detail.njk', { title: 'Transaction Detail', transaction: response.data.data });
    } catch (err) {
        res.render('error.njk', { title: 'Not Found', message: 'Transaction not found.', statusCode: 404 });
    }
});

module.exports = router;
