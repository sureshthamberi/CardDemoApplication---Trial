# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regression\accounts-payments-cards.spec.js >> Regression - Accounts, Payments, Cards >> account inquiry -> payment preview -> payment success
- Location: tests\regression\accounts-payments-cards.spec.js:14:3

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByText(/current balance/i)
Expected: visible
Timeout: 10000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 10000ms
  - waiting for getByText(/current balance/i)

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
  - alert:
    - heading "There is a problem" [level=2]
    - list:
      - listitem: You have nothing to pay
  - paragraph: Enter an account ID to check the balance and make a payment.
  - text: Account ID 11-digit account number
  - textbox "Account ID": "12345678901"
  - button "Check Balance"
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
  3  | const { currencyToNumber } = require('../utils/helpers');
  4  | 
  5  | class PaymentsPage extends BasePage {
  6  |   constructor(page) {
  7  |     super(page);
  8  |     this.accountId = page.locator('#accountId');
  9  |   }
  10 | 
  11 |   async openBillPayment() {
  12 |     await this.goto('/payments/bill');
  13 |     await this.expectHeading(/bill payment/i);
  14 |   }
  15 | 
  16 |   async previewPayment(accountId) {
  17 |     await this.accountId.fill(accountId);
  18 |     await this.clickButton(/check balance/i);
  19 |   }
  20 | 
  21 |   async assertPreviewVisible() {
> 22 |     await expect(this.page.getByText(/current balance/i)).toBeVisible();
     |                                                           ^ Error: expect(locator).toBeVisible() failed
  23 |     await expect(this.page.getByText(/balance after payment/i)).toBeVisible();
  24 |   }
  25 | 
  26 |   async verifyPreviewCalculation() {
  27 |     const currentBalanceText = await this.page.locator('.cd-stat-box__value').first().textContent();
  28 |     const afterPaymentText = await this.page.locator('.cd-stat-box__value').nth(1).textContent();
  29 | 
  30 |     const currentBalance = currencyToNumber(currentBalanceText);
  31 |     const afterPayment = currencyToNumber(afterPaymentText);
  32 | 
  33 |     expect(currentBalance).toBeGreaterThanOrEqual(0);
  34 |     expect(afterPayment).toBe(0);
  35 |   }
  36 | 
  37 |   async confirmPayment() {
  38 |     await this.clickButton(/confirm payment/i);
  39 |   }
  40 | 
  41 |   async assertPaymentSuccess() {
  42 |     await this.expectHeading(/payment successful/i);
  43 |     await expect(this.page.getByText(/transaction reference/i)).toBeVisible();
  44 |   }
  45 | 
  46 |   async verifySuccessCalculation() {
  47 |     const amountPaidText = await this.page.locator('.govuk-summary-list__row').filter({ hasText: 'Amount Paid' }).locator('.govuk-summary-list__value').textContent();
  48 |     const remainingBalanceText = await this.page.locator('.govuk-summary-list__row').filter({ hasText: 'Remaining Balance' }).locator('.govuk-summary-list__value').textContent();
  49 | 
  50 |     const amountPaid = currencyToNumber(amountPaidText);
  51 |     const remainingBalance = currencyToNumber(remainingBalanceText);
  52 | 
  53 |     expect(amountPaid).toBeGreaterThanOrEqual(0);
  54 |     expect(remainingBalance).toBe(0);
  55 |   }
  56 | 
  57 |   async assertPreviewError() {
  58 |     await expect(this.page.getByText(/preview failed|problem/i)).toBeVisible();
  59 |   }
  60 | }
  61 | 
  62 | module.exports = PaymentsPage;
  63 | 
```