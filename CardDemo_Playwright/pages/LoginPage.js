const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class LoginPage extends BasePage {
  constructor(page) {
    super(page);
    this.userIdInput = page.getByRole('textbox', { name: /user id/i });
    this.signInButton = page.getByRole('button', { name: /sign in|login/i });
  }

  async open() {
    await this.goto('/auth/login');
    await this.expectHeading(/sign in|login/i);
  }

  async login(userId, password) {
    await this.userIdInput.fill(userId);
    await this.page.getByRole('textbox', { name: /password/i }).fill(password).catch(async () => {
      await this.page.getByLabel(/password/i).fill(password);
    });
    await this.signInButton.click();
  }

  async loginAsStandard(user) {
    await this.open();
    await this.login(user.userId, user.password);
  }

  async loginAsAdmin(user) {
    await this.open();
    await this.login(user.userId, user.password);
  }

  async submitBlank() {
    await this.open();
    await this.signInButton.click();
  }

  async assertLoginPageVisible() {
    await this.expectHeading(/sign in|login/i);
    await expect(this.userIdInput).toBeVisible();
    await expect(this.signInButton).toBeVisible();
  }

  async assertValidationVisible() {
    await expect(this.page.getByText(/required|problem|enter/i)).toBeVisible();
  }

  async assertInvalidLoginError() {
    await expect(this.page.getByText(/invalid|incorrect|unable|problem/i)).toBeVisible();
  }
}

module.exports = LoginPage;
