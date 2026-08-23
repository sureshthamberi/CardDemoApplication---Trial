const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class MainMenuPage extends BasePage {
  async assertLoaded() {
    await expect(this.page.getByRole('heading', { name: /main menu|menu/i })).toBeVisible();
  }

  async openAccountsInquiry() {
    await this.page.getByRole('link', { name: /accounts|account inquiry/i }).click();
  }

  async openCards() {
    await this.page.getByRole('link', { name: /cards|card/i }).click();
  }

  async openPayments() {
    await this.page.getByRole('link', { name: /payments|bill payment/i }).click();
  }

  async openPendingAuthorizations() {
    await this.page.getByRole('link', { name: /pending auth|pending authorizations/i }).click();
  }

  async openReports() {
    await this.page.getByRole('link', { name: /reports|report request/i }).click();
  }

  async openTransactions() {
    await this.page.getByRole('link', { name: /transactions/i }).click();
  }

  async signOut() {
    await this.signOutIfVisible();
  }
}

module.exports = MainMenuPage;
