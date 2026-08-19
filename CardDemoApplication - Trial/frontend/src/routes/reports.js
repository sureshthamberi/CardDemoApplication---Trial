const express = require('express');
const router  = express.Router();
const { requireAuth } = require('../middleware/auth');
const { apiClient } = require('../config/apiClient');

router.use(requireAuth);

// GET /reports/requests — report request form
router.get('/requests', (req, res) => {
    res.render('pages/reports/request.njk', { title: 'Report Request', result: null, errors: null });
});

// POST /reports/requests — submit report request
router.post('/requests', async (req, res) => {
    const b = req.body;
    const { reportType, confirmation } = b;
    const errors = [];

    // Assemble ISO dates from govukDateInput day/month/year parts
    const startDay   = String(b['startDate-day']   || '').padStart(2,'0');
    const startMonth = String(b['startDate-month'] || '').padStart(2,'0');
    const startYear  = b['startDate-year'] || '';
    const endDay     = String(b['endDate-day']     || '').padStart(2,'0');
    const endMonth   = String(b['endDate-month']   || '').padStart(2,'0');
    const endYear    = b['endDate-year'] || '';
    const startDate = startYear ? `${startYear}-${startMonth}-${startDay}` : '';
    const endDate   = endYear   ? `${endYear}-${endMonth}-${endDay}`       : '';

    if (!reportType) errors.push({ text: 'Report type is required', href: '#reportType' });
    if (reportType === 'CUSTOM' && !startDate) errors.push({ text: 'Start date is required', href: '#startDate-day' });
    if (reportType === 'CUSTOM' && !endDate)   errors.push({ text: 'End date is required',   href: '#endDate-day' });
    if (!confirmation || confirmation !== 'Y') errors.push({ text: 'Confirmation is required', href: '#confirmation' });

    if (errors.length) {
        return res.render('pages/reports/request.njk', { title: 'Report Request', result: null, errors, formData: b });
    }

    try {
        const client = apiClient(req);
        const idempotencyKey = require('crypto').randomUUID();
        const response = await client.post('/reports/requests',
            { reportType, startDate: startDate || undefined, endDate: endDate || undefined, confirmation: 'Y' },
            { headers: { 'X-Idempotency-Key': idempotencyKey } }
        );
        res.render('pages/reports/request.njk', { title: 'Report Request', result: response.data.data, errors: null });
    } catch (err) {
        const msg = err.response && err.response.data ? err.response.data.message : 'Submission failed.';
        res.render('pages/reports/request.njk', { title: 'Report Request', result: null, errors: [{ text: msg }], formData: b });
    }
});

module.exports = router;
