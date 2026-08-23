const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const TransactionsPage = require('../../pages/TransactionsPage');
const testData = require('../../test-data/testData');

test.describe('Negative - Transactions', () => {
  test('transaction creation validates blank mandatory fields', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const transactionsPage = new TransactionsPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.openTransactions();
    await transactionsPage.openAddTransaction();

    await page.getByRole('button', { name: /submit|save|add/i }).click();
    await transactionsPage.assertValidationVisible();
  });
});
