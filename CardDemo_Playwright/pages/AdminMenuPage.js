const BasePage = require('./BasePage');

class AdminMenuPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/admin menu|main menu/i);
  }

  async openUsers() {
    await this.clickLink(/user/i);
  }

  async openReferenceData() {
    await this.clickLink(/transaction type|reference/i);
  }
}

module.exports = AdminMenuPage;
