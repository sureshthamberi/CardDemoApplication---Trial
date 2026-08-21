const { expect } = require('@playwright/test');

class BasePage {
  constructor(page) {
    this.page = page;
  }

  async goto(path) {
    await this.page.goto(path);
  }

  async expectHeading(text) {
    await expect(this.page.getByRole('heading', { name: text })).toBeVisible();
  }

  async clickLink(name) {
    await this.page.getByRole('link', { name }).click();
  }

  async clickButton(name) {
    await this.page.getByRole('button', { name }).click();
  }

  async expectText(text) {
    await expect(this.page.getByText(text, { exact: false })).toBeVisible();
  }

  async expectUrlContains(text) {
    await expect(this.page).toHaveURL(new RegExp(text));
  }

  async goBack() {
    await this.page.goBack();
  }

  async goForward() {
    await this.page.goForward();
  }
}

module.exports = BasePage;
