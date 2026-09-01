const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class ReportsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/reports|report request/i);
  }

  async submitMonthly() {
    const monthlyRadio = this.page.getByRole('radio', { name: /monthly/i });
    if (await monthlyRadio.count()) {
      await monthlyRadio.check();
    }
    await this.page.getByRole('radio', { name: /yes, submit this report/i }).check();
    await this.page.getByRole('button', { name: /submit|continue|request/i }).click();
  }

  async submitYearly() {
    const yearlyRadio = this.page.getByRole('radio', { name: /yearly/i });
    if (await yearlyRadio.count()) {
      await yearlyRadio.check();
    }
    await this.page.getByRole('radio', { name: /yes, submit this report/i }).check();
    await this.page.getByRole('button', { name: /submit|continue|request/i }).click();
  }

  async submitCustomRange(range) {
    const customRadio = this.page.getByRole('radio', { name: /custom/i });
    if (await customRadio.count()) {
      await customRadio.check();
    }

    const textboxes = this.page.getByRole('textbox');
    const dayCount = await textboxes.count();

    if (dayCount >= 6) {
      await textboxes.nth(0).fill(range.startDate.day);
      await textboxes.nth(1).fill(range.startDate.month);
      await textboxes.nth(2).fill(range.startDate.year);
      await textboxes.nth(3).fill(range.endDate.day);
      await textboxes.nth(4).fill(range.endDate.month);
      await textboxes.nth(5).fill(range.endDate.year);
    }

    await this.page.getByRole('radio', { name: /yes, submit this report/i }).check();
    await this.page.getByRole('button', { name: /submit|continue|request/i }).click();
  }

  async assertSubmitted() {
    await expect(this.page.locator('main')).toContainText(/submitted|requested|success|report generated/i);
  }

  async assertValidationVisible() {
    await expect(this.page.locator('.govuk-error-summary, .govuk-error-message').first()).toBeVisible();
  }
}

module.exports = ReportsPage;
