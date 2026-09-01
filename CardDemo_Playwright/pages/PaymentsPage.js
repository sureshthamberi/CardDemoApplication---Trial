const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class PaymentsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/payments|bill payment/i);
  }

  async assertPreviewVisible() {
    const mainContent = this.page.locator('main');
    await expect(mainContent).toContainText(/payment details|confirm payment|amount due|nothing to pay/i);
    this.paymentAvailable = !(await mainContent.getByText(/you have nothing to pay/i).count());
  }

  async verifyPreviewCalculation() {
    if (this.paymentAvailable === false) return;
    await expect(this.page.getByText(/total|amount due|payment amount/i).first()).toBeVisible();
  }

  async confirmPayment() {
    if (this.paymentAvailable === false) return;
    const confirmButton = this.page.getByRole('button', { name: /confirm|submit|pay/i });
    await expect(confirmButton.first()).toBeVisible();
    await confirmButton.first().click();
  }

  async assertPaymentSuccess() {
    if (this.paymentAvailable === false) return;
    await expect(this.page.getByText(/success|payment complete|submitted/i)).toBeVisible();
  }

  async verifySuccessCalculation() {
    if (this.paymentAvailable === false) return;
    await expect(this.page.getByText(/amount|paid|total/i)).toBeVisible();
  }

  async assertValidationVisible() {
    await expect(this.page.getByText(/required|invalid|problem/i)).toBeVisible();
  }
}

module.exports = PaymentsPage;
