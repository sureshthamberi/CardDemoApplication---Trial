# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: e2e\standard-user-e2e.spec.js >> E2E - Standard User >> standard user can complete end-to-end cross-module journey
- Location: tests\e2e\standard-user-e2e.spec.js:13:3

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByText(/total|amount due|payment amount/i).first()
Expected: visible
Timeout: 15000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 15000ms
  - waiting for getByText(/total|amount due|payment amount/i).first()

```

```yaml
- link "Skip to main content":
  - /url: "#main-content"
- banner:
  - link "GOV.UK CardDemo":
    - /url: /menu/main
  - text: Card Services Portal
  - navigation "Account navigation":
    - list:
      - listitem: ● Mary Smith STANDARD
      - listitem:
        - button "Sign out"
- paragraph:
  - strong: BETA
  - text: CardDemo Application — Development Environment
- link "Back to menu":
  - /url: /menu
- main:
  - text: Account Management
  - heading "Bill Payment" [level=1]
  - text: £450.75 Current Balance £0.00 Balance After Payment
  - paragraph:
    - text: Account
    - strong: "12345678901"
  - paragraph:
    - text: Confirming will pay the full balance of
    - strong: £450.75
    - text: and reduce your balance to
    - strong: £0.00
    - text: .
  - strong: Warning This action cannot be undone.
  - button "Confirm Payment"
  - link "Cancel":
    - /url: /payments/bill
- contentinfo:
  - heading "Support links" [level=2]
  - list:
    - listitem:
      - link "Menu":
        - /url: /menu/main
    - listitem:
      - link "API Docs":
        - /url: http://localhost:8080/swagger-ui.html
    - listitem:
      - link "H2 Console":
        - /url: http://localhost:8080/h2-console
    - listitem:
      - link "Health":
        - /url: http://localhost:8080/actuator/health
  - text: All content is available under the
  - link "Open Government Licence v3.0":
    - /url: https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/
  - text: ", except where otherwise stated"
  - link "© CardDemo Application 2026":
    - /url: https://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/crown-copyright/
```

# Test source

```ts
  1  | const { expect } = require('@playwright/test');
  2  | const BasePage = require('./BasePage');
  3  | 
  4  | class PaymentsPage extends BasePage {
  5  |   async assertLoaded() {
  6  |     await this.expectHeading(/payments|bill payment/i);
  7  |   }
  8  | 
  9  |   async assertPreviewVisible() {
  10 |     const mainContent = this.page.locator('main');
  11 |     await expect(mainContent).toContainText(/payment details|confirm payment|amount due|nothing to pay/i);
  12 |     this.paymentAvailable = !(await mainContent.getByText(/you have nothing to pay/i).count());
  13 |   }
  14 | 
  15 |   async verifyPreviewCalculation() {
  16 |     if (this.paymentAvailable === false) return;
> 17 |     await expect(this.page.getByText(/total|amount due|payment amount/i).first()).toBeVisible();
     |                                                                                   ^ Error: expect(locator).toBeVisible() failed
  18 |   }
  19 | 
  20 |   async confirmPayment() {
  21 |     if (this.paymentAvailable === false) return;
  22 |     const confirmButton = this.page.getByRole('button', { name: /confirm|submit|pay/i });
  23 |     await expect(confirmButton.first()).toBeVisible();
  24 |     await confirmButton.first().click();
  25 |   }
  26 | 
  27 |   async assertPaymentSuccess() {
  28 |     if (this.paymentAvailable === false) return;
  29 |     await expect(this.page.getByText(/success|payment complete|submitted/i)).toBeVisible();
  30 |   }
  31 | 
  32 |   async verifySuccessCalculation() {
  33 |     if (this.paymentAvailable === false) return;
  34 |     await expect(this.page.getByText(/amount|paid|total/i)).toBeVisible();
  35 |   }
  36 | 
  37 |   async assertValidationVisible() {
  38 |     await expect(this.page.getByText(/required|invalid|problem/i)).toBeVisible();
  39 |   }
  40 | }
  41 | 
  42 | module.exports = PaymentsPage;
  43 | 
```