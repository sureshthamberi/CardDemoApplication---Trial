const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class LoginPage extends BasePage {
  constructor(page) {
    super(page);
    this.userId = page.locator('#userId');
    this.password = page.locator('#password');
  }

  async open() {
    await this.goto('/auth/login');
    await this.expectHeading(/sign in/i);
  }

  async login(userId, password) {
    await this.userId.fill(userId);
    await this.password.fill(password);
    await this.clickButton(/sign in/i);
  }

  async loginAsStandard(data) {
    await this.open();
    await this.login(data.userId, data.password);
    await expect(this.page).toHaveURL(/\/menu\/main|\/menu\//);
  }

  async loginAsAdmin(data) {
    await this.open();
    await this.login(data.userId, data.password);
    await expect(this.page).toHaveURL(/\/menu\/admin|\/menu\//);
  }

  async assertLoginValidationErrors() {
    await expect(this.page.locator('#userId-error')).toContainText(/required/i);
    await expect(this.page.locator('#password-error')).toContainText(/required/i);
  }

  async assertInvalidCredentialError() {
    await expect(this.page.getByText(/invalid credentials|user not found|service unavailable/i).first()).toBeVisible();
  }
}

module.exports = LoginPage;
