const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class PendingAuthPage extends BasePage {
  constructor(page) {
    super(page);
    this.accountId = page.locator('#accountId');
  }

  async openSearch() {
    await this.goto('/pending-authorizations');
    await this.expectHeading(/pending authorizations/i);
  }

  async search(accountId) {
    await this.accountId.fill(accountId);
    await this.clickButton(/^search$/i);
  }

  async assertSearchPageError() {
    await expect(this.page.getByText(/search failed|problem/i)).toBeVisible();
  }

  async openDetail(authorizationId) {
    await this.goto(`/pending-authorizations/${authorizationId}`);
    await this.expectHeading(/authorization detail/i);
  }

  async assertDetailVisible() {
    await expect(this.page.getByText(/authorization details/i)).toBeVisible();
    await expect(this.page.getByText(/fraud actions/i)).toBeVisible();
  }

  async markFraudIfAvailable() {
    const markButton = this.page.getByRole('button', { name: /mark as fraud/i });
    if (await markButton.count()) {
      await markButton.click();
    }
  }

  async unmarkFraudIfAvailable() {
    const unmarkButton = this.page.getByRole('button', { name: /remove fraud flag/i });
    if (await unmarkButton.count()) {
      await unmarkButton.click();
    }
  }
}

module.exports = PendingAuthPage;
