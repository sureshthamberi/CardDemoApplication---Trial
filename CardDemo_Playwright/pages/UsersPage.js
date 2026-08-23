const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class UsersPage extends BasePage {
  async openList() {
    await this.goto('/admin/users');
    await expect(this.page.getByText(/user administration/i)).toBeVisible();
  }

  async openAdd() {
    await this.goto('/admin/users/add');
    await expect(this.page.getByText(/add user/i)).toBeVisible();
  }

  async addUser(user) {
    await this.page.locator('#firstName').fill(user.firstName);
    await this.page.locator('#lastName').fill(user.lastName);
    await this.page.locator('#userId').fill(user.userId);
    await this.page.locator('#password').fill(user.password);
    await this.page.locator('#userType').selectOption(user.userType);
    await this.clickButton(/add user|create|save/i);
  }

  async openEdit(userId) {
    await this.goto(`/admin/users/${userId}/edit`);
    await expect(this.page.getByRole('heading', { name: /update user/i })).toBeVisible();
  }

  async editUser(user) {
    await this.page.locator('#firstName').fill(user.firstName);
    await this.page.locator('#lastName').fill(user.lastName);
    await this.page.locator('#password').fill(user.password);
    await this.page.locator('#userType').selectOption(user.userType);
    await this.clickButton(/update user/i);
  }

  async openDelete(userId) {
    await this.goto(`/admin/users/${userId}/delete`);
  }

  async confirmDelete() {
    await this.clickButton(/delete|confirm/i);
  }
}

module.exports = UsersPage;
