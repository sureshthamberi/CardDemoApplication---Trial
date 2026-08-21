const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const AccountsPage = require('../../pages/AccountsPage');
const CardsPage = require('../../pages/CardsPage');
const PaymentsPage = require('../../pages/PaymentsPage');
const PendingAuthPage = require('../../pages/PendingAuthPage');
const ReportsPage = require('../../pages/ReportsPage');
const TransactionsPage = require('../../pages/TransactionsPage');
const data = require('../../test-data/testData');

test.describe('Smoke - Standard user main journeys', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);
  });

  test('menu navigation to all standard pages', async ({ page }) => {
    const menu = new MainMenuPage(page);
    const accounts = new AccountsPage(page);
    const cards = new CardsPage(page);
    const payments = new PaymentsPage(page);
    const pending = new PendingAuthPage(page);
    const reports = new ReportsPage(page);
    const transactions = new TransactionsPage(page);

    await menu.assertLoaded();

    await menu.openAccountsInquiry();
    await accounts.expectHeading(/account inquiry/i);
    await page.goBack();

    await menu.openBillPayment();
    await payments.expectHeading(/bill payment/i);
    await page.goBack();

    await menu.openCardSearch();
    await cards.expectHeading(/card search/i);
    await page.goBack();

    await menu.openTransactions();
    await transactions.expectHeading(/transactions/i);
    await page.goBack();

    await menu.openPendingAuthorizations();
    await pending.expectHeading(/pending authorizations/i);
    await page.goBack();

    await menu.openReportRequest();
    await reports.expectHeading(/report request/i);
  });
});
