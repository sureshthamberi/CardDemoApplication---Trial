const BasePage = require('./BasePage');

class MainMenuPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/main menu/i);
  }

  async openAccountsInquiry() {
    await this.clickLink(/account inquiry/i);
  }

  async openAccountUpdate() {
    await this.clickLink(/account update/i);
  }

  async openBillPayment() {
    await this.clickLink(/bill payment/i);
  }

  async openCardSearch() {
    await this.clickLink(/card search/i);
  }

  async openTransactions() {
    await this.clickLink(/transactions/i);
  }

  async openPendingAuthorizations() {
    await this.clickLink(/pending authorizations/i);
  }

  async openReportRequest() {
    await this.clickLink(/report request/i);
  }

  async signOut() {
    await this.page.getByRole('navigation', { name: 'Account navigation' })
      .getByRole('button', { name: 'Sign out' })
      .click();
  }
}

module.exports = MainMenuPage;
