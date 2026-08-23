const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class PendingAuthPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/pending auth|pending authorizations/i);
  }

  async openDetail(authId) {
    const authSearch = this.page.getByRole('textbox', { name: /authorization id|auth id/i });
    if (await authSearch.count()) {
      await authSearch.fill(authId);
      await this.page.getByRole('button', { name: /search|submit|continue/i }).click();
      return;
    }

    const authLink = this.page.getByRole('link', { name: new RegExp(authId, 'i') });
    if (await authLink.count()) {
      await authLink.first().click();
    }
  }

  async assertDetailVisible() {
    await expect(this.page.getByText(/authorization|merchant|amount|status/i)).toBeVisible();
  }

  async markFraudIfVisible() {
    const markButton = this.page.getByRole('button', { name: /mark fraud|fraud/i });
    if (await markButton.count()) {
      await markButton.first().click();
    }
  }

  async unmarkFraudIfVisible() {
    const unmarkButton = this.page.getByRole('button', { name: /unmark fraud|remove fraud/i });
    if (await unmarkButton.count()) {
      await unmarkButton.first().click();
    }
  }
}

module.exports = PendingAuthPage;
