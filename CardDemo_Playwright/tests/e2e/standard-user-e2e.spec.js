const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const AccountsPage = require('../../pages/AccountsPage');
const PaymentsPage = require('../../pages/PaymentsPage');
const CardsPage = require('../../pages/CardsPage');
const TransactionsPage = require('../../pages/TransactionsPage');
const ReportsPage = require('../../pages/ReportsPage');
const PendingAuthPage = require('../../pages/PendingAuthPage');
const testData = require('../../test-data/testData');

test.describe('E2E - Standard User', () => {
  test('standard user can complete end-to-end cross-module journey', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const accountsPage = new AccountsPage(page);
    const paymentsPage = new PaymentsPage(page);
    const cardsPage = new CardsPage(page);
    const transactionsPage = new TransactionsPage(page);
    const reportsPage = new ReportsPage(page);
    const pendingAuthPage = new PendingAuthPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.assertLoaded();

    await menuPage.openAccountsInquiry();
    await accountsPage.searchAccount(testData.accounts.validAccountId);
    await accountsPage.assertAccountDetailsVisible();

    await accountsPage.navigateToPayBill();
    await paymentsPage.assertPreviewVisible();
    await paymentsPage.verifyPreviewCalculation();
    await paymentsPage.confirmPayment();
    await paymentsPage.assertPaymentSuccess();

    await page.goto('/menu');
    await menuPage.openCards();
    await cardsPage.searchByCardNumber(testData.cards.validCardNumber);
    await cardsPage.assertSearchResultsOrNoFailure();

    await page.goto('/menu');
    await menuPage.openTransactions();
    await transactionsPage.assertLoaded();

    await page.goto('/menu');
    await menuPage.openReports();
    await reportsPage.submitMonthly();
    await reportsPage.assertSubmitted();

    await page.goto('/menu');
    await menuPage.openPendingAuthorizations();
    await pendingAuthPage.openDetail(testData.pendingAuth.validAuthorizationId);
    await pendingAuthPage.assertDetailVisible();

    await menuPage.signOut();
    await expect(page).toHaveURL(/auth/login/);
  });
});
