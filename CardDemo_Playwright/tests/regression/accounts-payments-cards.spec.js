const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const AccountsPage = require('../../pages/AccountsPage');
const PaymentsPage = require('../../pages/PaymentsPage');
const CardsPage = require('../../pages/CardsPage');
const data = require('../../test-data/testData');

test.describe('Regression - Accounts, Payments, Cards', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);
  });

  test('account inquiry -> payment preview -> payment success', async ({ page }) => {
    const accounts = new AccountsPage(page);
    const payments = new PaymentsPage(page);

    await accounts.openInquiry();
    await accounts.searchAccount(data.accounts.validAccountId);
    await accounts.assertAccountDetailsVisible();

    await accounts.navigateToPayBill();
    await payments.expectHeading(/bill payment/i);
    await payments.previewPayment(data.accounts.validAccountId);
    await payments.assertPreviewVisible();
    await payments.verifyPreviewCalculation();
    await payments.confirmPayment();
    await payments.assertPaymentSuccess();
    await payments.verifySuccessCalculation();
  });

  test('account inquiry -> cards navigation', async ({ page }) => {
    const accounts = new AccountsPage(page);
    const cards = new CardsPage(page);

    await accounts.openInquiry();
    await accounts.searchAccount(data.accounts.validAccountId);
    await accounts.assertAccountDetailsVisible();
    await accounts.navigateToViewCards();
    await cards.expectHeading(/card search/i);
    await cards.assertSearchResultsOrNoFailure();
  });

  test('card detail by valid card number', async ({ page }) => {
    const cards = new CardsPage(page);

    await cards.openDetail(data.cards.validCardNumber);
    await cards.assertCardDetailVisible();
  });

  test('card detail invalid card number shows error', async ({ page }) => {
    const cards = new CardsPage(page);

    await cards.openDetail(data.cards.invalidCardNumber);
    await cards.assertCardNotFound();
  });

  test('account inquiry invalid account shows error', async ({ page }) => {
    const accounts = new AccountsPage(page);

    await accounts.openInquiry();
    await accounts.searchAccount(data.accounts.invalidAccountId);
    await accounts.assertAccountError();
  });
});
