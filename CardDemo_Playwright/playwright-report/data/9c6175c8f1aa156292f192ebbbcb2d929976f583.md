# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regression\admin-users-reference.spec.js >> Regression - Admin users and reference maintenance >> reference data add edit delete
- Location: tests\regression\admin-users-reference.spec.js:13:3

# Error details

```
Test timeout of 60000ms exceeded.
```

```
Error: locator.fill: Test timeout of 60000ms exceeded.
Call log:
  - waiting for locator('#description')

```

# Page snapshot

```yaml
- generic [active] [ref=f4e1]:
  - link "Skip to main content" [ref=f4e2] [cursor=pointer]:
    - /url: "#main-content"
  - banner [ref=f4e3]:
    - generic [ref=f4e4]:
      - link "GOV.UK CardDemo" [ref=f4e6] [cursor=pointer]:
        - /url: /menu/admin
        - generic [ref=f4e7]: GOV.UK
        - generic [ref=f4e10]: CardDemo
      - generic [ref=f4e11]:
        - generic [ref=f4e12]: Card Services Portal
        - navigation "Account navigation" [ref=f4e13]:
          - list [ref=f4e14]:
            - listitem [ref=f4e15]:
              - generic [ref=f4e16]:
                - generic [ref=f4e17]: ★
                - text: John Doe
                - generic [ref=f4e18]: ADMIN
            - listitem [ref=f4e19]:
              - button "Sign out" [ref=f4e21] [cursor=pointer]
  - paragraph [ref=f4e23]:
    - strong [ref=f4e24]: BETA
    - generic [ref=f4e25]: CardDemo Application — Development Environment
  - main [ref=f4e27]:
    - generic [ref=f4e29]:
      - heading "Page not found" [level=1] [ref=f4e30]
      - paragraph [ref=f4e31]: If you typed the web address, check it is correct.
      - paragraph [ref=f4e32]: If you pasted the web address, check you copied the entire address.
      - button "Go to menu" [ref=f4e34] [cursor=pointer]
  - contentinfo [ref=f4e35]:
    - generic [ref=f4e37]:
      - generic [ref=f4e38]:
        - heading "Support links" [level=2] [ref=f4e39]
        - list [ref=f4e40]:
          - listitem [ref=f4e41]:
            - link "Menu" [ref=f4e42] [cursor=pointer]:
              - /url: /menu/admin
          - listitem [ref=f4e43]:
            - link "API Docs" [ref=f4e44] [cursor=pointer]:
              - /url: http://localhost:8080/swagger-ui.html
          - listitem [ref=f4e45]:
            - link "H2 Console" [ref=f4e46] [cursor=pointer]:
              - /url: http://localhost:8080/h2-console
          - listitem [ref=f4e47]:
            - link "Health" [ref=f4e48] [cursor=pointer]:
              - /url: http://localhost:8080/actuator/health
        - generic [ref=f4e51]:
          - text: All content is available under the
          - link "Open Government Licence v3.0" [ref=f4e52] [cursor=pointer]:
            - /url: https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/
          - text: ", except where otherwise stated"
      - link "© CardDemo Application 2026" [ref=f4e54] [cursor=pointer]:
        - /url: https://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/crown-copyright/
```

# Test source

```ts
  1  | const { expect } = require('@playwright/test');
  2  | const BasePage = require('./BasePage');
  3  | 
  4  | class ReferencePage extends BasePage {
  5  |   async openList() {
  6  |     await this.goto('/admin/transaction-types');
  7  |     await this.expectHeading(/transaction type maintenance/i);
  8  |   }
  9  | 
  10 |   async openAdd() {
  11 |     await this.goto('/admin/transaction-types/add');
  12 |     await expect(this.page.getByText(/add transaction type/i)).toBeVisible();
  13 |   }
  14 | 
  15 |   async addType(typeCode, description) {
  16 |     await this.page.locator('#typeCode').fill(typeCode, { force: true });
  17 |     await this.page.locator('#description').fill(description);
  18 |     await this.clickButton(/save|add|create/i);
  19 |   }
  20 | 
  21 |   async openEdit(typeCode) {
  22 |     await this.goto(`/admin/transaction-types/${typeCode}/edit`);
  23 |   }
  24 | 
  25 |   async updateDescription(description) {
> 26 |     await this.page.locator('#description').fill(description);
     |                                             ^ Error: locator.fill: Test timeout of 60000ms exceeded.
  27 |     await this.clickButton(/save|update/i);
  28 |   }
  29 | 
  30 |   async openDelete(typeCode) {
  31 |     await this.goto(`/admin/transaction-types/${typeCode}/delete`);
  32 |   }
  33 | 
  34 |   async confirmDelete() {
  35 |     await this.clickButton(/delete|confirm/i);
  36 |   }
  37 | }
  38 | 
  39 | module.exports = ReferencePage;
  40 | 
```