const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class TransactionsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/transactions/i);
  }

  async openAddTransaction() {
    await this.page.goto('/transactions/add');
  }

  async addTransaction(data) {
    await this.page.locator('input[name="accountId"]').fill(data.accountId);
    await this.page.locator('input[name="cardNumber"]').fill(data.cardNumber);

    const transactionSelect = this.page.locator('select[name="transactionType"]');
    const requestedType = (data.transactionType || data.categoryType || 'PURCHASE').toUpperCase();
    const typeValue = requestedType === 'RETAIL' ? 'PURCHASE' : requestedType;

    // Resolve the option before selecting it. Retrying through an async catch
    // handler can race with navigation and query a page that has been closed.
    const transactionOption = await transactionSelect.locator('option').evaluateAll((options, value) => {
      const match = options.find(option => {
        const text = (option.textContent || '').toUpperCase();
        return option.value.toUpperCase() === value || text.includes(value);
      });

      return match
        ? { value: match.value, label: (match.textContent || '').trim() }
        : null;
    }, typeValue);

    if (transactionOption) {
      await transactionSelect.selectOption(
        transactionOption.value
          ? { value: transactionOption.value }
          : { label: transactionOption.label }
      );
    }

    const categoryField = this.page.locator('input[name="category"], input[name="categoryType"]').first();
    if (await categoryField.count()) {
      await categoryField.fill(data.categoryType || requestedType);
    }

    const sourceSelect = this.page.locator('select[name="source"]');
    const sourceValue = String(data.source || 'ONLINE').toUpperCase();
    await sourceSelect.selectOption({ value: sourceValue }).catch(async () => {
      const sourceOptions = sourceSelect.locator('option');
      const count = await sourceOptions.count();
      for (let i = 0; i < count; i++) {
        const optionText = (await sourceOptions.nth(i).textContent()) || '';
        if (optionText.toUpperCase().includes(sourceValue)) {
          await sourceSelect.selectOption({ label: optionText.trim() });
          break;
        }
      }
    });

    await this.page.locator('input[name="amount"]').fill(data.amount);
    await this.page.locator('input[name="description"]').fill(data.description);

    await this.page.locator('input[name="originalDate-day"]').fill(data.originalDate.day);
    await this.page.locator('input[name="originalDate-month"]').fill(data.originalDate.month);
    await this.page.locator('input[name="originalDate-year"]').fill(data.originalDate.year);
    await this.page.locator('input[name="processDate-day"]').fill(data.processDate.day);
    await this.page.locator('input[name="processDate-month"]').fill(data.processDate.month);
    await this.page.locator('input[name="processDate-year"]').fill(data.processDate.year);

    const textboxes = this.page.getByRole('textbox');
    const count = await textboxes.count();

    if (count >= 10) {
      await textboxes.nth(count - 4).fill(data.merchantName);
      await textboxes.nth(count - 3).fill(data.merchantCity);
      await textboxes.nth(count - 2).fill(data.merchantId);
      await textboxes.nth(count - 1).fill(data.merchantZip);
    }

    await Promise.all([
      this.page.waitForLoadState('domcontentloaded'),
      this.page.locator('main form').evaluate(form => form.submit())
    ]);
  }

  async assertTransactionAdded() {
    await expect(this.page.locator('main')).toContainText(/success|added|created|submitted|saved|transaction/i, { timeout: 15000 });
  }

  async assertValidationVisible() {
    await expect(this.page.getByText(/required|invalid|problem/i)).toBeVisible();
  }
}

module.exports = TransactionsPage;
