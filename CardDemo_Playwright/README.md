# CardDemo_Playwright

Playwright automation framework for CardDemoApplication using JavaScript and Page Object Model.

## Scope Covered
This framework covers verified functional automation scope for:
- Auth
- Menu
- Accounts
- Cards
- Payments
- Pending Authorizations
- Reports
- Transactions
- Reference
- Users

## Test Coverage Types
- Smoke tests
- Regression tests
- End-to-end tests
- Negative tests
- Accessibility tests
- UI/UX tests
- Navigation and browser history validation
- Output verification where calculation or summary values are shown

## Folder Structure
```text
pages/
test-data/
utils/
tests/
  smoke/
  regression/
  e2e/
  negative/
  accessibility/
  uiux/
```

## Locator Strategy
Primary locator strategy used in this framework:
- `getByRole(...)`
- accessible names via labels/buttons/links/headings

This was implemented intentionally to align with accessibility-first and resilient locator practices.

## RTM - Requirement Traceability Matrix

| Requirement ID | Business Requirement | Coverage Type | Test File(s) |
|---|---|---|---|
| BR-001 | User can sign in with valid credentials | Smoke, E2E | `tests/smoke/auth-smoke.spec.js`, `tests/e2e/*.spec.js` |
| BR-002 | User cannot sign in with blank credentials | Negative, UI/UX | `tests/negative/login-negative.spec.js`, `tests/uiux/form-validation-uiux.spec.js` |
| BR-003 | User cannot sign in with invalid credentials | Negative | `tests/negative/login-negative.spec.js` |
| BR-004 | Standard user can navigate main menu options | Smoke, Regression, UI/UX | `tests/smoke/standard-user-smoke.spec.js`, `tests/regression/navigation-browser-history.spec.js`, `tests/uiux/navigation-uiux.spec.js` |
| BR-005 | Admin user can access admin areas | Smoke, E2E | `tests/smoke/admin-smoke.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-006 | Unauthenticated users are redirected to login for protected pages | Negative | `tests/negative/access-control.spec.js` |
| BR-007 | Standard user cannot access admin users page | Negative | `tests/negative/access-control.spec.js` |
| BR-008 | Standard user cannot access admin reference page | Negative | `tests/negative/access-control.spec.js` |
| BR-009 | User can enquire valid account details | Regression, E2E | `tests/regression/accounts-payments-cards.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-010 | Invalid account search is handled | Regression, Negative | `tests/regression/accounts-payments-cards.spec.js`, `tests/negative/accounts-negative.spec.js` |
| BR-011 | User can navigate from account inquiry to bill payment | Regression, E2E | `tests/regression/accounts-payments-cards.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-012 | User can preview bill payment for valid account | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-013 | Payment preview output values are validated | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-014 | User can confirm bill payment successfully | Regression, E2E | `tests/regression/accounts-payments-cards.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-015 | Payment success output values are validated | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-016 | User can search valid card details | Regression, E2E | `tests/regression/accounts-payments-cards.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-017 | Invalid card detail request is handled | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-018 | User can access transactions | Smoke, Regression, E2E | `tests/smoke/standard-user-smoke.spec.js`, `tests/regression/transactions-reports-pendingauth.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-019 | User can add transaction with valid data | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-020 | Transaction creation validates required fields | Negative | `tests/negative/transactions-negative.spec.js` |
| BR-021 | User can request monthly report | Regression, E2E | `tests/regression/transactions-reports-pendingauth.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-022 | User can request yearly report | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-023 | User can request custom date range report | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-024 | Report request requires mandatory selections and inputs | Negative | `tests/negative/report-negative.spec.js` |
| BR-025 | User can view pending authorization detail | Regression, E2E | `tests/regression/transactions-reports-pendingauth.spec.js`, `tests/e2e/standard-user-e2e.spec.js` |
| BR-026 | User can perform fraud mark/unmark actions where supported | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-027 | Admin can add transaction/reference type | Regression, E2E | `tests/regression/admin-users-reference.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-028 | Admin can edit transaction/reference type | Regression, E2E | `tests/regression/admin-users-reference.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-029 | Admin can delete transaction/reference type | Regression, E2E | `tests/regression/admin-users-reference.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-030 | Admin can add user | Regression, E2E | `tests/regression/admin-users-reference.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-031 | Admin can edit user | Regression, E2E | `tests/regression/admin-users-reference.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-032 | Admin can delete user | Regression, E2E | `tests/regression/admin-users-reference.spec.js`, `tests/e2e/admin-user-e2e.spec.js` |
| BR-033 | Browser back/forward navigation works across supported pages | Regression, UI/UX | `tests/regression/navigation-browser-history.spec.js`, `tests/uiux/navigation-uiux.spec.js` |
| BR-034 | User can sign out | Smoke, E2E | `tests/smoke/auth-smoke.spec.js`, `tests/e2e/*.spec.js` |
| BR-035 | Core pages expose accessible roles/headings/actions | Accessibility | `tests/accessibility/*.spec.js` |
| BR-036 | Pages remain usable in smaller viewports | UI/UX | `tests/uiux/responsive-smoke-uiux.spec.js` |

## Scope Missed / Pending Live Validation
The following items may require live DOM tuning in your environment:
- exact accessible names of some buttons/links/fields
- exact route URLs for some direct `page.goto(...)` navigations
- specific success/error texts
- specific dropdown option values
- exact control naming for pending authorization and admin maintenance pages

## Assumptions
- application uses accessible roles and labels for major controls
- standard and admin users are seeded and valid
- account/card/reference/user data is available in the environment
- routes remain stable for `/auth/login`, `/menu`, and feature pages

## Test Data Requirements
Configured in:
- `test-data/testData.js`

Includes:
- valid standard user
- valid admin user
- invalid user
- account IDs
- card numbers
- report date range
- pending auth data
- transaction input data
- admin reference/user CRUD data

## Run Instructions
```bash
npm install
npx playwright install
npm test
```

## Run by Suite
```bash
npm run test:smoke
npm run test:regression
npm run test:e2e
npm run test:negative
npm run test:accessibility
npm run test:uiux
```

## Recommendation
For maximum stability and full compliance with `getByRole` locator strategy:
- add explicit accessible labels to all fields
- add unique accessible button names
- add stable `data-testid` only where semantic locators are not feasible
- seed deterministic lower environment data for repeatable execution
