const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class TransactionsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/transactions/i);
  }

  async openAddTransaction() {
    const addButton = this.page.getByRole('button', { name: /add transaction|add/i });
    const addLink = this.page.getByRole('link', { name: /add transaction|add/i });

    if (await addButton.count()) {
      await addButton.first().click();
      return;
    }

    if (await addLink.count()) {
      await addLink.first().click();
    }
  }

  async addTransaction(data) {
    await this.fillTextbox(/account id|account number/i, data.accountId);
    await this.fillTextbox(/card number/i, data.cardNumber);

    await this.selectOption(/category|category type/i, data.categoryType).catch(() => {});
    await this.selectOption(/source/i, data.source).catch(() => {});

    await this.fillTextbox(/amount/i, data.amount);
    await this.fillTextbox(/description/i, data.description);

    const textboxes = this.page.getByRole('textbox');
    const count = await textboxes.count();

    if (count >= 10) {
      await textboxes.nth(count - 4).fill(data.merchantName);
      await textboxes.nth(count - 3).fill(data.merchantCity);
      await textboxes.nth(count - 2).fill(data.merchantId);
      await textboxes.nth(count - 1).fill(data.merchantZip);
    }

    await this.page.getByRole('button', { name: /submit|save|add/i }).click();
  }

  async assertTransactionAdded() {
    await expect(this.page.getByText(/success|added|created|submitted/i)).toBeVisible();
  }

  async assertValidationVisible() {
    await expect(this.page.getByText(/required|invalid|problem/i)).toBeVisible();
  }
}

module.exports = TransactionsPage;
