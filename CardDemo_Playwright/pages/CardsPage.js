const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class CardsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/cards|card detail|card search/i);
  }

  async searchByCardNumber(cardNumber) {
    await this.page.getByRole('textbox', { name: /card number/i }).fill(cardNumber);
    await this.page.getByRole('button', { name: /search|submit|continue/i }).click();
  }

  async assertCardDetailsVisible() {
    await expect(this.page.getByText(/card/i)).toBeVisible();
  }

  async assertSearchResultsOrNoFailure() {
    const detailText = this.page.getByText(/card|expiry|status|account/i);
    await expect(detailText.first()).toBeVisible();
  }

  async assertInvalidCardHandled() {
    await expect(this.page.getByText('No cards found for the given search criteria.', { exact: true })).toBeVisible();
  }
}

module.exports = CardsPage;
