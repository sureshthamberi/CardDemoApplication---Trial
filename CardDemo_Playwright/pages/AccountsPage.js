const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class AccountsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/accounts|account inquiry/i);
  }

  async searchAccount(accountId) {
    await this.page.getByRole('textbox', { name: /account id|account number/i }).fill(accountId);
    await this.page.getByRole('button', { name: /search|submit|continue/i }).click();
  }

  async assertAccountDetailsVisible() {
    await expect(this.page.getByText(/account/i)).toBeVisible();
  }

  async assertInvalidSearchHandled() {
    await expect(this.page.getByText(/not found|invalid|unable|problem/i)).toBeVisible();
  }

  async navigateToPayBill() {
    const payBillLink = this.page.getByRole('link', { name: /pay bill|bill payment|make payment/i });
    const payBillButton = this.page.getByRole('button', { name: /pay bill|bill payment|make payment/i });

    if (await payBillLink.count()) {
      await payBillLink.first().click();
      return;
    }

    if (await payBillButton.count()) {
      await payBillButton.first().click();
    }
  }
}

module.exports = AccountsPage;
