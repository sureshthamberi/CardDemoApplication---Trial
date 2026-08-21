const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class ReportsPage extends BasePage {
  constructor(page) {
    super(page);
  }

  async open() {
    await this.goto('/reports/requests');
    await this.expectHeading(/report request/i);
  }

  async submitMonthly(confirm = true) {
    await this.page.getByLabel(/monthly/i).check();
    if (confirm) await this.page.getByLabel(/yes, submit this report/i).check();
    await this.clickButton(/submit request/i);
  }

  async submitYearly(confirm = true) {
    await this.page.getByLabel(/yearly/i).check();
    if (confirm) await this.page.getByLabel(/yes, submit this report/i).check();
    await this.clickButton(/submit request/i);
  }

  async submitCustom(range, confirm = true) {
    await this.page.getByLabel(/custom date range/i).check();
    await this.page.locator('#startDate-day').fill(range.startDate.day);
    await this.page.locator('#startDate-month').fill(range.startDate.month);
    await this.page.locator('#startDate-year').fill(range.startDate.year);
    await this.page.locator('#endDate-day').fill(range.endDate.day);
    await this.page.locator('#endDate-month').fill(range.endDate.month);
    await this.page.locator('#endDate-year').fill(range.endDate.year);
    if (confirm) await this.page.getByLabel(/yes, submit this report/i).check();
    await this.clickButton(/submit request/i);
  }

  async assertSubmitted() {
    await expect(this.page.getByText(/request submitted/i)).toBeVisible();
    await expect(this.page.getByText(/request id/i)).toBeVisible();
  }

  async assertValidationErrors() {
    await expect(this.page.getByText(/there is a problem/i)).toBeVisible();
  }
}

module.exports = ReportsPage;
