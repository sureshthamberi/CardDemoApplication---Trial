const nunjucks = require('nunjucks');
const path = require('path');
const env = nunjucks.configure(
  [path.join('src','views'), path.join('node_modules','govuk-frontend','dist','govuk')],
  { autoescape: true }
);
env.addFilter('currency', v => '£0.00');
env.addFilter('maskCard', v => '');
env.addFilter('formatDate', v => '');

const out = env.render('pages/reports/request.njk', {
  user: { displayName: 'Test', userType: 'ADMIN' },
  flash: null,
  formData: {}
});

const hasDataModule   = out.includes('data-module="govuk-radios"');
const hasAriaControls = out.includes('data-aria-controls');
const hasHidden       = out.includes('govuk-radios__conditional--hidden');
const hasStartDay     = out.includes('id="startDate-day"');
const hasEndDay       = out.includes('id="endDate-day"');

console.log('data-module="govuk-radios" present :', hasDataModule);
console.log('data-aria-controls on CUSTOM radio  :', hasAriaControls);
console.log('Conditional hidden by default       :', hasHidden);
console.log('startDate-day input rendered        :', hasStartDay);
console.log('endDate-day input rendered          :', hasEndDay);

// Find the CUSTOM radio input element
const customRadioMatch = out.match(/value="CUSTOM"[^\n]*/);
console.log('\nCUSTOM radio input line:', customRadioMatch ? customRadioMatch[0].trim() : 'NOT FOUND');
