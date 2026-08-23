const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const AccountsPage = require('../../pages/AccountsPage');
const { expectCommonAccessibility } = require('../../utils/a11y');
const testData = require('../../test-data/testData');

test.describe('Accessibility - Payments', () => {
  test('payments flow page has accessible content', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const accountsPage = new AccountsPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.openAccountsInquiry();
    await accountsPage.searchAccount(testData.accounts.validAccountId);
    await accountsPage.navigateToPayBill();

    await expectCommonAccessibility(page, /payments|bill payment/i);
  });
});
