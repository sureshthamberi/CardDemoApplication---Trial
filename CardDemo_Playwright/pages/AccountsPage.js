const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class AccountsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/accounts|account inquiry/i);
  }

  async searchAccount(accountId) {
    this.accountId = accountId;
    await this.page.getByRole('textbox', { name: /account id|account number/i }).fill(accountId);
    await this.page.getByRole('button', { name: /enquire|search|submit|continue/i }).click();
  }

  async assertAccountDetailsVisible() {
    await expect(this.page.getByText('Account Details', { exact: true })).toBeVisible();
  }

  async assertInvalidSearchHandled() {
    await expect(this.page.locator('.govuk-error-summary, .govuk-error-message').first()).toBeVisible();
  }

  async navigateToPayBill() {
    await this.page.goto('/payments/bill');

    const accountInput = this.page.getByRole('textbox', { name: /account id|account number/i });
    if (await accountInput.count() && this.accountId) {
      await accountInput.fill(this.accountId);
      await Promise.all([
        this.page.waitForLoadState('domcontentloaded'),
        this.page.locator('form[action="/payments/bill/preview"]').evaluate(form => form.submit())
      ]);
    }
  }
}

module.exports = AccountsPage;
