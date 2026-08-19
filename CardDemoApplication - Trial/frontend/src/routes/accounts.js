const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

router.use(requireAuth);

// GET /accounts/inquiry — show inquiry form
router.get('/inquiry', (req, res) => {
    res.render('pages/accounts/inquiry.njk', { title: 'Account Inquiry', account: null, errors: null });
});

// POST /accounts/inquiry — lookup account
router.post('/inquiry', async (req, res) => {
    const { accountId } = req.body;
    try {
        const client = apiClient(req);
        const response = await client.get(`/accounts/${accountId}`);
        const data = response.data.data;
        res.render('pages/accounts/inquiry.njk', { title: 'Account Inquiry', account: data, errors: null, accountId });
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Account not found.';
        res.render('pages/accounts/inquiry.njk', { title: 'Account Inquiry', account: null, errors: [{ text: msg }], accountId });
    }
});

// GET /accounts/update — show update form (query param ?accountId=...)
router.get('/update', async (req, res) => {
    const { accountId } = req.query;
    if (!accountId) return res.render('pages/accounts/inquiry.njk', { title: 'Account Update', account: null, errors: null, updateMode: true });
    try {
        const client = apiClient(req);
        const response = await client.get(`/accounts/${accountId}`);
        res.render('pages/accounts/update.njk', { title: 'Account Update', data: response.data.data, errors: null });
    } catch (err) {
        res.render('error.njk', { title: 'Error', message: 'Account not found.', statusCode: 404 });
    }
});

// POST /accounts/update/:accountId — submit update
router.post('/update/:accountId', async (req, res) => {
    const { accountId } = req.params;
    const body = req.body;
    // Assemble expirationDate from govukDateInput day/month/year parts
    const expDay   = String(body['expirationDate-day']   || '').padStart(2,'0');
    const expMonth = String(body['expirationDate-month'] || '').padStart(2,'0');
    const expYear  = body['expirationDate-year'] || '';
    const expirationDate = expYear ? `${expYear}-${expMonth}-${expDay}` : undefined;
    try {
        const client = apiClient(req);
        await client.put(`/accounts/${accountId}`, {
            account: {
                accountStatus: body.accountStatus,
                creditLimit: parseFloat(body.creditLimit),
                cashCreditLimit: parseFloat(body.cashCreditLimit),
                expirationDate
            },
            customer: {
                firstName: body.firstName,
                lastName: body.lastName,
                addressLine1: body.addressLine1,
                city: body.city,
                state: body.state,
                zip: body.zip,
                phone1: body.phone1,
                primaryCardHolderIndicator: body.primaryCardHolderIndicator
            },
            accountRowVersion: parseInt(body.accountRowVersion, 10),
            customerRowVersion: parseInt(body.customerRowVersion, 10)
        });
        req.session.flash = { success: 'Account updated successfully.' };
        res.redirect(`/accounts/inquiry`);
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Update failed.';
        res.render('pages/accounts/update.njk', { title: 'Account Update', data: req.body, errors: [{ text: msg }] });
    }
});

module.exports = router;
