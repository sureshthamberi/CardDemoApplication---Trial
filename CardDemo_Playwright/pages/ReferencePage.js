const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class ReferencePage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/reference/i);
  }

  async openAdd() {
    await this.page.getByRole('button', { name: /add/i }).click().catch(async () => {
      await this.page.getByRole('link', { name: /add/i }).click();
    });
  }

  async addType(typeCode, description) {
    await this.fillTextbox(/type code|code/i, typeCode);
    await this.fillTextbox(/description/i, description);
    await this.page.getByRole('button', { name: /save|submit|add/i }).click();
  }

  async openEdit(typeCode) {
    const editButton = this.page.getByRole('button', { name: new RegExp(`edit.*${typeCode}|${typeCode}.*edit`, 'i') });
    if (await editButton.count()) {
      await editButton.first().click();
      return;
    }

    await this.page.getByRole('button', { name: /edit/i }).first().click();
  }

  async updateDescription(description) {
    await this.fillTextbox(/description/i, description);
    await this.page.getByRole('button', { name: /save|update/i }).click();
  }

  async openDelete(typeCode) {
    const deleteButton = this.page.getByRole('button', { name: new RegExp(`delete.*${typeCode}|${typeCode}.*delete`, 'i') });
    if (await deleteButton.count()) {
      await deleteButton.first().click();
      return;
    }

    await this.page.getByRole('button', { name: /delete/i }).first().click();
  }

  async confirmDelete() {
    await this.page.getByRole('button', { name: /confirm|delete/i }).click();
  }

  async assertSuccessVisible() {
    await expect(this.page.getByText(/success|saved|updated|deleted/i)).toBeVisible();
  }
}

module.exports = ReferencePage;
