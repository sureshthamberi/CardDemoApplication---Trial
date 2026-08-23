const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class AdminMenuPage extends BasePage {
  async assertLoaded() {
    await expect(this.page.getByRole('heading', { name: /menu|admin/i })).toBeVisible();
  }

  async openReferenceData() {
    await this.page.getByRole('link', { name: /reference/i }).click();
  }

  async openUsers() {
    await this.page.getByRole('link', { name: /users/i }).click();
  }

  async signOut() {
    await this.signOutIfVisible();
  }
}

module.exports = AdminMenuPage;
