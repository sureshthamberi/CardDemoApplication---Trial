const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const AccountsPage = require('../../pages/AccountsPage');
const testData = require('../../test-data/testData');

test.describe('Negative - Accounts', () => {
  test('account inquiry validates blank and invalid input', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const accountsPage = new AccountsPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.openAccountsInquiry();

    await page.getByRole('button', { name: /search|submit|continue/i }).click();
    await accountsPage.assertInvalidSearchHandled();

    await accountsPage.searchAccount(testData.accounts.invalidAccountId);
    await accountsPage.assertInvalidSearchHandled();
  });
});
