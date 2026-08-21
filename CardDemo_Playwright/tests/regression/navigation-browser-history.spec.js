const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const data = require('../../test-data/testData');

test.describe('Regression - Navigation and browser history', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);
  });

  test('navigate across pages using links and browser back/forward', async ({ page }) => {
    const menu = new MainMenuPage(page);

    await menu.openAccountsInquiry();
    await expect(page).toHaveURL(/\/accounts\/inquiry/);

    await page.goBack();
    await expect(page).toHaveURL(/\/menu/);

    await page.goForward();
    await expect(page).toHaveURL(/\/accounts\/inquiry/);

    await page.goto('/cards/search');
    await expect(page).toHaveURL(/\/cards\/search/);

    await page.goBack();
    await expect(page).toHaveURL(/\/accounts\/inquiry|\/menu/);

    await page.goto('/payments/bill');
    await expect(page).toHaveURL(/\/payments\/bill/);

    await page.goto('/transactions');
    await expect(page).toHaveURL(/\/transactions/);

    await page.goto('/pending-authorizations');
    await expect(page).toHaveURL(/\/pending-authorizations/);

    await page.goto('/reports/requests');
    await expect(page).toHaveURL(/\/reports\/requests/);
  });
});
