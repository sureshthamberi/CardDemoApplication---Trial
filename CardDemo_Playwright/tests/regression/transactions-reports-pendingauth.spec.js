const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const TransactionsPage = require('../../pages/TransactionsPage');
const ReportsPage = require('../../pages/ReportsPage');
const PendingAuthPage = require('../../pages/PendingAuthPage');
const testData = require('../../test-data/testData');

test.describe('Regression - Transactions, Reports, Pending Auth', () => {
  test('user can access transactions, submit reports and view pending auth', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const transactionsPage = new TransactionsPage(page);
    const reportsPage = new ReportsPage(page);
    const pendingAuthPage = new PendingAuthPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.assertLoaded();

    await menuPage.openTransactions();
    await transactionsPage.assertLoaded();
    await transactionsPage.openAddTransaction();
    await transactionsPage.addTransaction(testData.transactions.addTransaction);
    await transactionsPage.assertTransactionAdded();

    await page.goto('/menu');
    await menuPage.openReports();
    await reportsPage.assertLoaded();
    await reportsPage.submitMonthly();
    await reportsPage.assertSubmitted();

    await page.goto('/menu');
    await menuPage.openReports();
    await reportsPage.submitYearly();
    await reportsPage.assertSubmitted();

    await page.goto('/menu');
    await menuPage.openReports();
    await reportsPage.submitCustomRange(testData.reports.customRange);
    await reportsPage.assertSubmitted();

    await page.goto('/menu');
    await menuPage.openPendingAuthorizations();
    await pendingAuthPage.assertLoaded();
    await pendingAuthPage.openDetail(testData.pendingAuth.validAuthorizationId);
    await pendingAuthPage.assertDetailVisible();
    await pendingAuthPage.markFraudIfVisible();
    await pendingAuthPage.unmarkFraudIfVisible();
  });
});
