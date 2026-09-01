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
    const select = this.page.getByRole('combobox', { name: labelPattern });
    const optionValue = String(value);

    await select.selectOption({ label: optionValue }).catch(async () => {
      await select.selectOption({ value: optionValue }).catch(async () => {
        const option = select.locator('option').filter({
          hasText: new RegExp(optionValue.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'i')
        }).first();

        if (await option.count()) {
          await select.selectOption({ label: (await option.textContent()).trim() });
          return;
        }

        throw new Error(`No option matched "${value}" for ${labelPattern}`);
      });
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
