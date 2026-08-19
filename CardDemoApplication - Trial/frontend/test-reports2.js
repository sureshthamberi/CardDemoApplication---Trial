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

// Find the conditional div content
const match = out.match(/id="conditional-reportType-4"[^>]*>([\s\S]*?)<\/div>\n\s*<\/div>\n\s*<\/div>/);
if (match) {
  const inner = match[1].trim();
  console.log('Conditional div inner content length:', inner.length);
  console.log('First 200 chars of inner content:');
  console.log(JSON.stringify(inner.substring(0, 200)));
} else {
  console.log('Could not find conditional div');
  // Search for the id in the output
  const idx = out.indexOf('conditional-reportType-4');
  if (idx >= 0) {
    console.log('Found at index', idx);
    console.log(out.substring(idx, idx + 400));
  }
}
