const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class ReferencePage extends BasePage {
  async openList() {
    await this.goto('/admin/transaction-types');
    await this.expectHeading(/transaction type maintenance/i);
  }

  async openAdd() {
    await this.goto('/admin/transaction-types/add');
    await expect(this.page.getByText(/add transaction type/i)).toBeVisible();
  }

  async addType(typeCode, description) {
    await this.page.locator('#typeCode').fill(typeCode);
    await this.page.locator('#description').fill(description);
    await this.clickButton(/save|add|create/i);
  }

  async openEdit(typeCode) {
    await this.goto(`/admin/transaction-types/${typeCode}/edit`);
  }

  async updateDescription(description) {
    await this.page.locator('#description').fill(description);
    await this.clickButton(/save|update/i);
  }

  async openDelete(typeCode) {
    await this.goto(`/admin/transaction-types/${typeCode}/delete`);
  }

  async confirmDelete() {
    await this.clickButton(/delete|confirm/i);
  }
}

module.exports = ReferencePage;
