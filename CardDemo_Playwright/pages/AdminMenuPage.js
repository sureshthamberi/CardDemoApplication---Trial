const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class AdminMenuPage extends BasePage {
  async assertLoaded() {
    await expect(this.page.getByRole('heading', { name: 'Admin Menu', exact: true })).toBeVisible();
  }

  async openReferenceData() {
    await this.page.locator('a[href="/admin/transaction-types"]').click();
  }

  async openUsers() {
    await this.page.locator('a[href="/admin/users"]').click();
  }

  async signOut() {
    await this.signOutIfVisible();
  }
}

module.exports = AdminMenuPage;
