const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');
const { currencyToNumber } = require('../utils/helpers');

class PaymentsPage extends BasePage {
  constructor(page) {
    super(page);
    this.accountId = page.locator('#accountId');
  }

  async openBillPayment() {
    await this.goto('/payments/bill');
    await this.expectHeading(/bill payment/i);
  }

  async previewPayment(accountId) {
    await this.accountId.fill(accountId);
    await this.clickButton(/check balance/i);
  }

  async assertPreviewVisible() {
    await expect(this.page.getByText(/current balance/i)).toBeVisible();
    await expect(this.page.getByText(/balance after payment/i)).toBeVisible();
  }

  async verifyPreviewCalculation() {
    const currentBalanceText = await this.page.locator('.cd-stat-box__value').first().textContent();
    const afterPaymentText = await this.page.locator('.cd-stat-box__value').nth(1).textContent();

    const currentBalance = currencyToNumber(currentBalanceText);
    const afterPayment = currencyToNumber(afterPaymentText);

    expect(currentBalance).toBeGreaterThanOrEqual(0);
    expect(afterPayment).toBe(0);
  }

  async confirmPayment() {
    await this.clickButton(/confirm payment/i);
  }

  async assertPaymentSuccess() {
    await this.expectHeading(/payment successful/i);
    await expect(this.page.getByText(/transaction reference/i)).toBeVisible();
  }

  async verifySuccessCalculation() {
    const amountPaidText = await this.page.locator('.govuk-summary-list__row').filter({ hasText: 'Amount Paid' }).locator('.govuk-summary-list__value').textContent();
    const remainingBalanceText = await this.page.locator('.govuk-summary-list__row').filter({ hasText: 'Remaining Balance' }).locator('.govuk-summary-list__value').textContent();

    const amountPaid = currencyToNumber(amountPaidText);
    const remainingBalance = currencyToNumber(remainingBalanceText);

    expect(amountPaid).toBeGreaterThanOrEqual(0);
    expect(remainingBalance).toBe(0);
  }

  async assertPreviewError() {
    await expect(this.page.getByText(/preview failed|problem/i)).toBeVisible();
  }
}

module.exports = PaymentsPage;
