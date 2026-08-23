const { expect } = require('@playwright/test');

class BasePage {
  constructor(page) {
    this.page = page;
  }

  async goto(path) {
    await this.page.goto(path);
  }

  async expectHeading(namePattern) {
    await expect(this.page.getByRole('heading', { name: namePattern })).toBeVisible();
  }

  async clickLink(namePattern) {
    await this.page.getByRole('link', { name: namePattern }).click();
  }

  async clickButton(namePattern) {
    await this.page.getByRole('button', { name: namePattern }).click();
  }

  async fillTextbox(labelPattern, value) {
    await this.page.getByRole('textbox', { name: labelPattern }).fill(value);
  }

  async selectOption(labelPattern, value) {
    await this.page.getByRole('combobox', { name: labelPattern }).selectOption({ label: value }).catch(async () => {
      await this.page.getByRole('combobox', { name: labelPattern }).selectOption(value);
    });
  }

  async expectTextVisible(textPattern) {
    await expect(this.page.getByText(textPattern)).toBeVisible();
  }

  async signOutIfVisible() {
    const signOutButton = this.page.getByRole('button', { name: /sign out/i });
    const signOutLink = this.page.getByRole('link', { name: /sign out/i });

    if (await signOutButton.count()) {
      await signOutButton.first().click();
      return;
    }

    if (await signOutLink.count()) {
      await signOutLink.first().click();
    }
  }
}

module.exports = BasePage;
