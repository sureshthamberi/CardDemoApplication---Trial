const express      = require('express');
const nunjucks     = require('nunjucks');
const path         = require('path');
const cookieParser = require('cookie-parser');
const session      = require('express-session');
const morgan       = require('morgan');

const authRoutes         = require('./routes/auth');
const menuRoutes         = require('./routes/menu');
const userRoutes         = require('./routes/users');
const accountRoutes      = require('./routes/accounts');
const paymentRoutes      = require('./routes/payments');
const cardRoutes         = require('./routes/cards');
const transactionRoutes  = require('./routes/transactions');
const referenceRoutes    = require('./routes/reference');
const pendingAuthRoutes  = require('./routes/pendingauth');
const reportRoutes       = require('./routes/reports');

const app  = express();
const PORT = process.env.PORT || 3000;

// =============================================================
// Middleware
// =============================================================
app.use(morgan('dev'));
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(cookieParser());
app.use(session({
    secret: process.env.SESSION_SECRET || 'carddemo-secret-2024',
    resave: false,
    saveUninitialized: false,
    cookie: { maxAge: 86400000 } // 24 hours
}));

// =============================================================
// Static assets — GOV.UK Frontend
// =============================================================
app.use('/assets', express.static(
    path.join(__dirname, '..', 'node_modules', 'govuk-frontend', 'dist', 'govuk', 'assets')
));
app.use('/govuk', express.static(
    path.join(__dirname, '..', 'node_modules', 'govuk-frontend', 'dist')
));
app.use('/public', express.static(path.join(__dirname, 'public')));

// =============================================================
// Nunjucks templating
// =============================================================
const nunjucksEnv = nunjucks.configure(
    [
        path.join(__dirname, 'views'),
        path.join(__dirname, '..', 'node_modules', 'govuk-frontend', 'dist', 'govuk')
    ],
    {
        autoescape: true,
        express: app,
        noCache: process.env.NODE_ENV !== 'production'
    }
);

nunjucksEnv.addGlobal('appName', 'CardDemo');
nunjucksEnv.addFilter('currency', (value) => {
    if (value === null || value === undefined) return '£0.00';
    return '£' + parseFloat(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
});
nunjucksEnv.addFilter('maskCard', (value) => {
    if (!value) return '';
    return '**** **** **** ' + String(value).slice(-4);
});
nunjucksEnv.addFilter('formatDate', (value) => {
    if (!value) return '';
    const d = new Date(value);
    return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' });
});

app.set('view engine', 'njk');

// =============================================================
// Inject session user + flash messages into all templates
// Flash is consumed once and then cleared from the session.
// =============================================================
app.use((req, res, next) => {
    res.locals.user        = req.session.user || null;
    res.locals.currentPath = req.path;
    // Consume flash once
    if (req.session.flash) {
        res.locals.flash   = req.session.flash;
        req.session.flash  = null;
    } else {
        res.locals.flash   = null;
    }
    next();
});

// =============================================================
// Routes
// =============================================================
app.get('/', (req, res) => res.redirect('/auth/login'));

app.use('/auth',         authRoutes);
app.use('/menu',         menuRoutes);
app.use('/admin/users',  userRoutes);
app.use('/accounts',     accountRoutes);
app.use('/payments',     paymentRoutes);
app.use('/cards',        cardRoutes);
app.use('/transactions', transactionRoutes);
app.use('/admin/transaction-types', referenceRoutes);
app.use('/pending-authorizations',  pendingAuthRoutes);
app.use('/reports',      reportRoutes);

// =============================================================
// 404 handler
// =============================================================
app.use((req, res) => {
    res.status(404).render('error.njk', {
        title: 'Page not found',
        message: 'The page you requested could not be found.',
        statusCode: 404
    });
});

// =============================================================
// Error handler
// =============================================================
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).render('error.njk', {
        title: 'Sorry, there is a problem with the service',
        message: err.message || 'An unexpected error occurred.',
        statusCode: 500
    });
});

// =============================================================
// Start server
// =============================================================
app.listen(PORT, () => {
    console.log(`CardDemo Frontend running at http://localhost:${PORT}`);
});

module.exports = app;
