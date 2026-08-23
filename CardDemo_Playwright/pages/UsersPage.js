const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class UsersPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/users/i);
  }

  async openAdd() {
    await this.page.getByRole('button', { name: /add/i }).click().catch(async () => {
      await this.page.getByRole('link', { name: /add/i }).click();
    });
  }

  async addUser(user) {
    await this.fillTextbox(/first name/i, user.firstName);
    await this.fillTextbox(/last name/i, user.lastName);
    await this.fillTextbox(/user id/i, user.userId);
    await this.fillTextbox(/password/i, user.password);
    await this.selectOption(/user type|role/i, user.userType).catch(() => {});
    await this.page.getByRole('button', { name: /save|submit|add/i }).click();
  }

  async openEdit(userId) {
    const editButton = this.page.getByRole('button', { name: new RegExp(`edit.*${userId}|${userId}.*edit`, 'i') });
    if (await editButton.count()) {
      await editButton.first().click();
      return;
    }

    await this.page.getByRole('button', { name: /edit/i }).first().click();
  }

  async editUser(user) {
    await this.fillTextbox(/first name/i, user.firstName);
    await this.fillTextbox(/last name/i, user.lastName);
    await this.fillTextbox(/password/i, user.password);
    await this.selectOption(/user type|role/i, user.userType).catch(() => {});
    await this.page.getByRole('button', { name: /save|update/i }).click();
  }

  async openDelete(userId) {
    const deleteButton = this.page.getByRole('button', { name: new RegExp(`delete.*${userId}|${userId}.*delete`, 'i') });
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

  async assertValidationVisible() {
    await expect(this.page.getByText(/required|invalid|problem/i)).toBeVisible();
  }
}

module.exports = UsersPage;
