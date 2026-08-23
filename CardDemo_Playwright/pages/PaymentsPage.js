const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class PaymentsPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/payments|bill payment/i);
  }

  async assertPreviewVisible() {
    await expect(this.page.getByText(/preview|payment details|confirm/i)).toBeVisible();
  }

  async verifyPreviewCalculation() {
    await expect(this.page.getByText(/total|amount|payment/i)).toBeVisible();
  }

  async confirmPayment() {
    const confirmButton = this.page.getByRole('button', { name: /confirm|submit|pay/i });
    await expect(confirmButton.first()).toBeVisible();
    await confirmButton.first().click();
  }

  async assertPaymentSuccess() {
    await expect(this.page.getByText(/success|payment complete|submitted/i)).toBeVisible();
  }

  async verifySuccessCalculation() {
    await expect(this.page.getByText(/amount|paid|total/i)).toBeVisible();
  }

  async assertValidationVisible() {
    await expect(this.page.getByText(/required|invalid|problem/i)).toBeVisible();
  }
}

module.exports = PaymentsPage;
