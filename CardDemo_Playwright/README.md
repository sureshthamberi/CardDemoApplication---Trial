# CardDemo_Playwright

Playwright automation framework for CardDemoApplication using JavaScript and Page Object Model (POM).

## Scope Covered
This framework covers verified end-to-end user journeys and supporting validations across the application modules:
- Accounts
- Auth
- Cards
- Menu
- Payments
- Pending Authorizations
- Reports
- Transactions
- Reference Data
- Users

## Test Coverage Types
- Smoke tests
- Regression tests
- Negative tests
- End-to-end flows
- Navigation and browser history validation
- Calculation/output verification where UI values are available

## RTM - Requirement Traceability Matrix

| Requirement ID | Business Requirement | Coverage Type | Test File(s) |
|---|---|---|---|
| BR-001 | User can sign in with valid credentials | Smoke, Regression Foundation | `tests/smoke/auth-smoke.spec.js` |
| BR-002 | User cannot sign in with blank credentials | Negative | `tests/negative/login-negative.spec.js` |
| BR-003 | User cannot sign in with invalid credentials | Negative | `tests/negative/login-negative.spec.js` |
| BR-004 | Standard user can navigate main menu options | Smoke, Regression | `tests/smoke/standard-user-smoke.spec.js`, `tests/regression/navigation-browser-history.spec.js` |
| BR-005 | Admin user can access admin areas | Smoke | `tests/smoke/admin-smoke.spec.js` |
| BR-006 | Unauthenticated users are redirected to login for protected pages | Negative | `tests/negative/access-control.spec.js` |
| BR-007 | Standard user cannot access admin users page | Negative | `tests/negative/access-control.spec.js` |
| BR-008 | Standard user cannot access admin reference page | Negative | `tests/negative/access-control.spec.js` |
| BR-009 | User can enquire valid account details | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-010 | Invalid account search is handled | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-011 | User can navigate from account inquiry to bill payment | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-012 | User can preview bill payment for valid account | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-013 | Payment preview output values are validated | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-014 | User can confirm bill payment successfully | Regression, E2E | `tests/regression/accounts-payments-cards.spec.js` |
| BR-015 | Payment success output values are validated | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-016 | User can navigate from accounts to cards | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-017 | User can view valid card detail | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-018 | Invalid card detail request is handled | Regression | `tests/regression/accounts-payments-cards.spec.js` |
| BR-019 | User can access transactions list | Smoke, Regression | `tests/smoke/standard-user-smoke.spec.js`, `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-020 | User can add transaction with valid data | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-021 | User can request monthly report | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-022 | User can request yearly report | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-023 | User can request custom date range report | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-024 | Report request requires mandatory selections and inputs | Negative | `tests/negative/report-negative.spec.js` |
| BR-025 | User can view pending authorization detail | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-026 | User can perform fraud mark/unmark actions where allowed | Regression | `tests/regression/transactions-reports-pendingauth.spec.js` |
| BR-027 | Admin can add transaction type | Regression | `tests/regression/admin-users-reference.spec.js` |
| BR-028 | Admin can edit transaction type | Regression | `tests/regression/admin-users-reference.spec.js` |
| BR-029 | Admin can delete transaction type | Regression | `tests/regression/admin-users-reference.spec.js` |
| BR-030 | Admin can add user | Regression | `tests/regression/admin-users-reference.spec.js` |
| BR-031 | Admin can edit user | Regression | `tests/regression/admin-users-reference.spec.js` |
| BR-032 | Admin can delete user | Regression | `tests/regression/admin-users-reference.spec.js` |
| BR-033 | Browser back/forward navigation works across supported pages | Regression | `tests/regression/navigation-browser-history.spec.js` |
| BR-034 | User can sign out | Smoke | `tests/smoke/auth-smoke.spec.js` |

## Requirements/Assumptions
Before execution, update test data in `test-data/testData.js` with environment-valid values.

Required data:
- valid standard user
- valid admin user
- valid and invalid account ids
- valid and invalid card numbers
- valid pending authorization id
- transaction creation data
- admin data for user/reference maintenance

## Framework Highlights
- JavaScript Playwright
- Page Object Model
- Environment-driven test data
- Clear suite separation
- Reusable helpers
- Ready for future CI integration

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
npm run test:negative
```

## Recommendation
For long-term stability, add `data-testid` attributes in the application UI and seed deterministic lower-environment test data.
