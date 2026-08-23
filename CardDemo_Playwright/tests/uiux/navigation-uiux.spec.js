const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const AccountsPage = require('../../pages/AccountsPage');
const testData = require('../../test-data/testData');

test.describe('UI/UX - Navigation', () => {
  test('user can navigate with links and browser navigation controls', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const accountsPage = new AccountsPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.assertLoaded();

    await menuPage.openAccountsInquiry();
    await accountsPage.assertLoaded();

    await page.goBack();
    await expect(page.getByRole('heading', { name: /main menu|menu/i })).toBeVisible();

    await page.goForward();
    await expect(page.getByRole('heading', { name: /accounts|account inquiry/i })).toBeVisible();
  });
});
