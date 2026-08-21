const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class AccountsPage extends BasePage {
  constructor(page) {
    super(page);
    this.accountId = page.locator('#accountId');
    this.creditLimit = page.locator('#creditLimit');
    this.cashCreditLimit = page.locator('#cashCreditLimit');
    this.firstName = page.locator('#firstName');
    this.lastName = page.locator('#lastName');
    this.addressLine1 = page.locator('#addressLine1');
    this.city = page.locator('#city');
    this.state = page.locator('#state');
    this.zip = page.locator('#zip');
    this.phone1 = page.locator('#phone1');
  }

  async openInquiry() {
    await this.goto('/accounts/inquiry');
    await this.expectHeading(/account inquiry/i);
  }

  async searchAccount(accountId) {
    await this.accountId.fill(accountId);
    await this.clickButton(/enquire/i);
  }

  async assertAccountDetailsVisible() {
    await expect(this.page.getByText(/account details/i)).toBeVisible();
    await expect(this.page.getByText(/customer details/i)).toBeVisible();
  }

  async assertAccountError() {
    await expect(this.page.getByText(/account not found|problem/i)).toBeVisible();
  }

  async openUpdateFromInquiry() {
    await this.clickButton(/update account/i);
  }

  async assertUpdateLoaded() {
    await this.expectHeading(/account update/i);
  }

  async updateAccount(form) {
    if (form.creditLimit) await this.creditLimit.fill(form.creditLimit);
    if (form.cashCreditLimit) await this.cashCreditLimit.fill(form.cashCreditLimit);
    if (form.firstName) await this.firstName.fill(form.firstName);
    if (form.lastName) await this.lastName.fill(form.lastName);
    if (form.addressLine1) await this.addressLine1.fill(form.addressLine1);
    if (form.city) await this.city.fill(form.city);
    if (form.state) await this.state.fill(form.state);
    if (form.zip) await this.zip.fill(form.zip);
    if (form.phone1) await this.phone1.fill(form.phone1);
    await this.clickButton(/save changes/i);
  }

  async assertUpdateSuccess() {
    await expect(this.page).toHaveURL(/\/accounts\/inquiry/);
  }

  async navigateToPayBill() {
    await this.clickButton(/pay bill/i);
  }

  async navigateToViewCards() {
    await this.clickButton(/view cards/i);
  }

  async navigateToViewTransactions() {
    await this.clickButton(/view transactions/i);
  }
}

module.exports = AccountsPage;
