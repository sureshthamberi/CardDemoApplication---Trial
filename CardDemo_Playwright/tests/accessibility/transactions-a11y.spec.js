const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const { expectCommonAccessibility } = require('../../utils/a11y');
const testData = require('../../test-data/testData');

test.describe('Accessibility - Transactions', () => {
  test('transactions page has accessible heading and actions', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.openTransactions();

    await expectCommonAccessibility(page, /transactions/i);
  });
});
