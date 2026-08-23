# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regression\transactions-reports-pendingauth.spec.js >> Regression - Transactions, Reports, Pending Authorizations >> report request custom range
- Location: tests\regression\transactions-reports-pendingauth.spec.js:40:3

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByText(/request submitted/i)
Expected: visible
Timeout: 10000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 10000ms
  - waiting for getByText(/request submitted/i)

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
  - text: Reporting
  - heading "Report Request" [level=1]
  - alert:
    - heading "There is a problem" [level=2]
    - list:
      - listitem:
        - link "Start date is required":
          - /url: "#startDate-day"
      - listitem:
        - link "End date is required":
          - /url: "#endDate-day"
  - group "Report Type":
    - text: Report Type Select the reporting period
    - radio "Monthly"
    - text: Monthly Current calendar month
    - radio "Yearly"
    - text: Yearly Current calendar year or
    - radio "Custom date range" [checked]
    - text: Custom date range
    - group "Start Date":
      - text: Start Date For example, 1 6 2026 Day
      - textbox "Day"
      - text: Month
      - textbox "Month"
      - text: Year
      - textbox "Year"
    - group "End Date":
      - text: End Date For example, 30 6 2026 Day
      - textbox "Day"
      - text: Month
      - textbox "Month"
      - text: Year
      - textbox "Year"
  - separator
  - group "Confirmation":
    - text: Confirmation Confirm you want to submit this report request
    - radio "Yes, submit this report" [checked]
    - text: Yes, submit this report
    - radio "No"
    - text: "No"
  - button "Submit Request"
  - link "Cancel":
    - /url: /menu
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
  4  | class ReportsPage extends BasePage {
  5  |   constructor(page) {
  6  |     super(page);
  7  |   }
  8  | 
  9  |   async open() {
  10 |     await this.goto('/reports/requests');
  11 |     await this.expectHeading(/report request/i);
  12 |   }
  13 | 
  14 |   async submitMonthly(confirm = true) {
  15 |     await this.page.getByLabel(/monthly/i).check();
  16 |     if (confirm) await this.page.getByLabel(/yes, submit this report/i).check();
  17 |     await this.clickButton(/submit request/i);
  18 |   }
  19 | 
  20 |   async submitYearly(confirm = true) {
  21 |     await this.page.getByLabel(/yearly/i).check();
  22 |     if (confirm) await this.page.getByLabel(/yes, submit this report/i).check();
  23 |     await this.clickButton(/submit request/i);
  24 |   }
  25 | 
  26 |   async submitCustom(range, confirm = true) {
  27 |     await this.page.getByRole('radio', { name: /custom date range/i }).click();
  28 |     await this.page.locator('#startDate-day').fill(range.startDate.day, { force: true });
  29 |     await this.page.locator('#startDate-month').fill(range.startDate.month, { force: true });
  30 |     await this.page.locator('#startDate-year').fill(range.startDate.year, { force: true });
  31 |     await this.page.locator('#endDate-day').fill(range.endDate.day, { force: true });
  32 |     await this.page.locator('#endDate-month').fill(range.endDate.month, { force: true });
  33 |     await this.page.locator('#endDate-year').fill(range.endDate.year, { force: true });
  34 |     if (confirm) await this.page.getByLabel(/yes, submit this report/i).check();
  35 |     await this.clickButton(/submit request/i);
  36 |   }
  37 | 
  38 |   async assertSubmitted() {
> 39 |     await expect(this.page.getByText(/request submitted/i)).toBeVisible();
     |                                                             ^ Error: expect(locator).toBeVisible() failed
  40 |     await expect(this.page.getByText(/^Request ID$/, { exact: true })).toBeVisible();
  41 |   }
  42 | 
  43 |   async assertValidationErrors() {
  44 |     await expect(this.page.getByText(/there is a problem/i)).toBeVisible();
  45 |   }
  46 | }
  47 | 
  48 | module.exports = ReportsPage;
  49 | 
```