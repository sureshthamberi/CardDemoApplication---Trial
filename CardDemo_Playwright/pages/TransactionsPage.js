const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class TransactionsPage extends BasePage {
  async openList(accountId = '') {
    const url = accountId ? `/transactions?accountId=${accountId}` : '/transactions';
    await this.goto(url);
    await this.expectHeading(/transactions/i);
  }

  async openAdd() {
    await this.goto('/transactions/add');
    await this.expectHeading(/add transaction/i);
  }

  async fillAddForm(data) {
    await this.page.locator('#accountId').fill(data.accountId);
    await this.page.locator('#cardNumber').fill(data.cardNumber);
    const transactionType = this.page.locator('#transactionType');
    if (await transactionType.locator('option').count() > 1) {
      await transactionType.selectOption({ index: 1 });
    }
    await this.page.locator('#categoryType').fill(data.categoryType);
    await this.page.locator('#source').selectOption(data.source);
    await this.page.locator('#amount').fill(data.amount);
    await this.page.locator('#description').fill(data.description);

    await this.page.locator('#originalDate-day').fill(data.originalDate.day);
    await this.page.locator('#originalDate-month').fill(data.originalDate.month);
    await this.page.locator('#originalDate-year').fill(data.originalDate.year);

    await this.page.locator('#processDate-day').fill(data.processDate.day);
    await this.page.locator('#processDate-month').fill(data.processDate.month);
    await this.page.locator('#processDate-year').fill(data.processDate.year);

    await this.page.locator('#merchantName').fill(data.merchantName);
    await this.page.locator('#merchantCity').fill(data.merchantCity);
    await this.page.locator('#merchantId').fill(data.merchantId);
    await this.page.locator('#merchantZip').fill(data.merchantZip);
  }

  async submitTransaction() {
    await this.clickButton(/submit transaction/i);
  }

  async assertRedirectedToList() {
    await expect(this.page).toHaveURL(/\/transactions/);
  }

  async openDetail(transactionId) {
    await this.goto(`/transactions/${transactionId}`);
    await this.expectHeading(/transaction detail/i);
  }
}

module.exports = TransactionsPage;
