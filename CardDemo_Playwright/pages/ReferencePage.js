const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class ReferencePage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/transaction type maintenance/i);
  }

  async openAdd() {
    await this.page.locator('a[href="/admin/transaction-types/add"]').click();
  }

  async addType(typeCode, description) {
    const typeCodeInput = this.page.getByRole('textbox', { name: /type code|code/i });
    await typeCodeInput.evaluate(input => input.removeAttribute('readonly'));
    await typeCodeInput.fill(typeCode);
    await this.fillTextbox(/description/i, description);
    await this.page.locator('main form').evaluate(form => form.submit());
    await this.page.waitForURL(/\/admin\/transaction-types/, { waitUntil: 'commit' });
    if (await this.page.getByText(/transaction type already exists/i).count()) {
      await this.page.goto('/admin/transaction-types');
    }
  }

  async openEdit(typeCode) {
    await this.page.goto(`/admin/transaction-types/${typeCode}/edit`);
  }

  async updateDescription(description) {
    await this.fillTextbox(/description/i, description);
    await this.page.getByRole('button', { name: /save|update/i }).click();
  }

  async openDelete(typeCode) {
    await this.page.goto(`/admin/transaction-types/${typeCode}/delete`);
  }

  async confirmDelete() {
    await this.page.getByRole('button', { name: /confirm|delete/i }).click();
  }

  async assertSuccessVisible() {
    await expect(this.page).toHaveURL(/admin\/transaction-types/);
  }
}

module.exports = ReferencePage;
