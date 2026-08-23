const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const ReportsPage = require('../../pages/ReportsPage');
const data = require('../../test-data/testData');

test.describe('Negative - Report validation', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);
  });

  test('submit with no selections shows validation', async ({ page }) => {
    const reports = new ReportsPage(page);
    await reports.open();
    await reports.clickButton(/submit request/i);
    await reports.assertValidationErrors();
  });

  test('custom report without dates shows validation', async ({ page }) => {
    const reports = new ReportsPage(page);
    await reports.open();
    await page.getByLabel(/custom date range/i).check();
    await page.getByRole('radio', { name: /^yes, submit this report$/i }).check();
    await reports.clickButton(/submit request/i);
    await reports.assertValidationErrors();
  });

  test('monthly report without confirmation shows validation', async ({ page }) => {
    const reports = new ReportsPage(page);
    await reports.open();
    await page.getByLabel(/monthly/i).check();
    await reports.clickButton(/submit request/i);
    await reports.assertValidationErrors();
  });
});
