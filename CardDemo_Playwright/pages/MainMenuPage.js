const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class MainMenuPage extends BasePage {
  async assertLoaded() {
    await expect(this.page.getByRole('heading', { name: /main menu|menu/i })).toBeVisible();
  }

  async openAccountsInquiry() {
    await this.page.getByRole('link', { name: /account inquiry/i }).click();
  }

  async openCards() {
    await this.page.locator('a[href="/cards/search"]').click();
  }

  async openPayments() {
    await this.page.locator('a[href="/payments/bill"]').click();
  }

  async openPendingAuthorizations() {
    await this.page.locator('a[href="/pending-authorizations"]').click();
  }

  async openReports() {
    await this.page.locator('a[href="/reports/requests"]').click();
  }

  async openTransactions() {
    await this.page.locator('a[href="/transactions"]').click();
  }

  async signOut() {
    await this.signOutIfVisible();
  }
}

module.exports = MainMenuPage;
