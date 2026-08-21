const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class CardsPage extends BasePage {
  constructor(page) {
    super(page);
    this.accountId = page.locator('#accountId');
    this.cardNumber = page.locator('#cardNumber');
    this.cardName = page.locator('#cardName');
  }

  async openSearch() {
    await this.goto('/cards/search');
    await this.expectHeading(/card search/i);
  }

  async searchByAccount(accountId) {
    await this.accountId.fill(accountId);
    await this.page.keyboard.press('Enter');
  }

  async searchByCardNumber(cardNumber) {
    await this.cardNumber.fill(cardNumber);
    await this.page.keyboard.press('Enter');
  }

  async assertSearchResultsOrNoFailure() {
    await expect(this.page.getByRole('heading', { name: /card search/i })).toBeVisible();
  }

  async openDetail(cardNumber) {
    await this.goto(`/cards/detail?cardNumber=${cardNumber}`);
    await this.expectHeading(/card detail/i);
  }

  async assertCardDetailVisible() {
    await expect(this.page.getByText(/card detail/i)).toBeVisible();
  }

  async assertCardNotFound() {
    await expect(this.page.getByText(/card not found|problem/i)).toBeVisible();
  }

  async openEdit(cardNumber) {
    await this.goto(`/cards/${cardNumber}/edit`);
    await this.expectHeading(/update card/i);
  }

  async updateCard(cardName) {
    await this.cardName.fill(cardName);
    await this.clickButton(/save|update/i);
  }
}

module.exports = CardsPage;
