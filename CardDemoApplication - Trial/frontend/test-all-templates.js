const nunjucks = require('nunjucks');
const path = require('path');

const env = nunjucks.configure(
  [
    path.join('src', 'views'),
    path.join('node_modules', 'govuk-frontend', 'dist', 'govuk')
  ],
  { autoescape: true }
);
env.addFilter('currency', v => '£' + parseFloat(v||0).toFixed(2));
env.addFilter('maskCard', v => !v ? '' : '****' + v.slice(-4));
env.addFilter('formatDate', v => !v ? '' : new Date(v).toLocaleDateString('en-GB'));

const ctx = { user: { displayName: 'John Doe', userType: 'ADMIN' }, flash: null };

const pages = [
  'layouts/base.njk',
  'error.njk',
  'pages/auth/login.njk',
  'pages/menu/main.njk',
  'pages/menu/admin.njk',
  'pages/accounts/inquiry.njk',
  'pages/accounts/update.njk',
  'pages/payments/bill.njk',
  'pages/payments/success.njk',
  'pages/cards/search.njk',
  'pages/cards/detail.njk',
  'pages/cards/edit.njk',
  'pages/transactions/list.njk',
  'pages/transactions/detail.njk',
  'pages/transactions/add.njk',
  'pages/users/list.njk',
  'pages/users/add.njk',
  'pages/users/edit.njk',
  'pages/users/delete.njk',
  'pages/reference/list.njk',
  'pages/reference/form.njk',
  'pages/pendingauth/search.njk',
  'pages/pendingauth/list.njk',
  'pages/pendingauth/detail.njk',
  'pages/reports/request.njk'
];

let pass = 0, fail = 0;
pages.forEach(p => {
  try {
    const out = env.render(p, ctx);
    // Verify govuk macros actually rendered (no raw macro call text remaining)
    if (out.includes('govukButton(') || out.includes('govukInput(')) {
      console.error('  ✗ MACRO NOT EXPANDED:', p);
      fail++;
    } else {
      console.log('  ✓', p);
      pass++;
    }
  } catch(e) {
    console.error('  ✗', p, '→', e.message.substring(0, 120));
    fail++;
  }
});

console.log('');
console.log(pass + '/' + pages.length + ' templates OK' + (fail ? ' — ' + fail + ' FAILED' : ''));
process.exit(fail > 0 ? 1 : 0);
