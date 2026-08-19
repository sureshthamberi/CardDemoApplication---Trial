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

// Test 1: govukButton macro
try {
  const r = env.renderString(
    `{% from "components/button/macro.njk" import govukButton %}` +
    `{{ govukButton({ text: "Test Button" }) }}`
  );
  console.log('govukButton macro OK');
} catch(e) { console.error('govukButton FAIL:', e.message); }

// Test 2: govukInput macro
try {
  const r = env.renderString(
    `{% from "components/input/macro.njk" import govukInput %}` +
    `{{ govukInput({ label: { text: "Name" }, id: "name", name: "name" }) }}`
  );
  console.log('govukInput macro OK');
} catch(e) { console.error('govukInput FAIL:', e.message); }

// Test 3: govukSelect macro
try {
  const r = env.renderString(
    `{% from "components/select/macro.njk" import govukSelect %}` +
    `{{ govukSelect({ label: { text: "Type" }, id: "type", name: "type", items: [{ value: "A", text: "Option A" }] }) }}`
  );
  console.log('govukSelect macro OK');
} catch(e) { console.error('govukSelect FAIL:', e.message); }

// Test 4: govukRadios macro
try {
  const r = env.renderString(
    `{% from "components/radios/macro.njk" import govukRadios %}` +
    `{{ govukRadios({ name: "choice", fieldset: { legend: { text: "Choose" } }, items: [{ value: "Y", text: "Yes" }, { value: "N", text: "No" }] }) }}`
  );
  console.log('govukRadios macro OK');
} catch(e) { console.error('govukRadios FAIL:', e.message); }

// Test 5: govukDateInput macro
try {
  const r = env.renderString(
    `{% from "components/date-input/macro.njk" import govukDateInput %}` +
    `{{ govukDateInput({ id: "dob", namePrefix: "dob", fieldset: { legend: { text: "Date of birth" } } }) }}`
  );
  console.log('govukDateInput macro OK');
} catch(e) { console.error('govukDateInput FAIL:', e.message); }

// Test 6: govukErrorSummary macro
try {
  const r = env.renderString(
    `{% from "components/error-summary/macro.njk" import govukErrorSummary %}` +
    `{{ govukErrorSummary({ titleText: "There is a problem", errorList: [{ text: "Enter your name", href: "#name" }] }) }}`
  );
  console.log('govukErrorSummary macro OK');
} catch(e) { console.error('govukErrorSummary FAIL:', e.message); }

// Test 7: base.njk with macros
try {
  env.render('layouts/base.njk', { user: { displayName: 'Test', userType: 'ADMIN' }, flash: null });
  console.log('base.njk OK');
} catch(e) { console.error('base.njk FAIL:', e.message.substring(0, 200)); }

// Test 8: partials/govuk-macros.njk import
try {
  env.renderString(`{% import "partials/govuk-macros.njk" as macros %}OK`);
  console.log('govuk-macros partial import OK');
} catch(e) { console.error('govuk-macros partial FAIL:', e.message); }
