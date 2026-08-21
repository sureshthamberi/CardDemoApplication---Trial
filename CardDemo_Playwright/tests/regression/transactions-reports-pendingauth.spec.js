const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const TransactionsPage = require('../../pages/TransactionsPage');
const ReportsPage = require('../../pages/ReportsPage');
const PendingAuthPage = require('../../pages/PendingAuthPage');
const data = require('../../test-data/testData');

test.describe('Regression - Transactions, Reports, Pending Authorizations', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);
  });

  test('transactions list and add transaction flow', async ({ page }) => {
    const transactions = new TransactionsPage(page);

    await transactions.openList(data.accounts.validAccountId);
    await transactions.openAdd();
    await transactions.fillAddForm(data.transactions.addTransaction);
    await transactions.submitTransaction();
    await transactions.assertRedirectedToList();
  });

  test('report request monthly', async ({ page }) => {
    const reports = new ReportsPage(page);

    await reports.open();
    await reports.submitMonthly(true);
    await reports.assertSubmitted();
  });

  test('report request yearly', async ({ page }) => {
    const reports = new ReportsPage(page);

    await reports.open();
    await reports.submitYearly(true);
    await reports.assertSubmitted();
  });

  test('report request custom range', async ({ page }) => {
    const reports = new ReportsPage(page);

    await reports.open();
    await reports.submitCustom(data.reports.customRange, true);
    await reports.assertSubmitted();
  });

  test('pending auth detail and fraud toggle', async ({ page }) => {
    const pending = new PendingAuthPage(page);

    await pending.openDetail(data.pendingAuth.validAuthorizationId);
    await pending.assertDetailVisible();
    await pending.markFraudIfAvailable();
    await pending.unmarkFraudIfAvailable();
  });
});
