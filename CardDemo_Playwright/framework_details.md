# CardDemo Playwright Framework Details

## Overview
This folder contains a Playwright automation framework in JavaScript for the CardDemoApplication. The framework follows Page Object Model (POM) and is organized for smoke, regression, and negative coverage.

## Design Principles
- Avoid hallucinated workflows by using only verified routes and UI modules.
- Avoid redundant test journeys.
- Separate smoke, regression, and negative coverage.
- Keep test data configurable.
- Provide reusable page object classes.
- Include output verification where calculations are visible in the UI.

## Modules Covered
- Auth
- Menu
- Accounts
- Cards
- Payments
- Pending Authorizations
- Reports
- Transactions
- Reference Data
- Users

## Folder Structure
- `pages/` Page Object classes
- `tests/smoke/` smoke suite
- `tests/regression/` regression suite
- `tests/negative/` negative suite
- `test-data/` environment-specific input data
- `utils/` helper functions

## Suites
### Smoke
- authentication
- standard user navigation
- admin basic access

### Regression
- account inquiry to bill payment flow
- accounts to cards navigation
- card detail positive and negative
- transaction add flow
- report request flows
- pending authorization fraud actions
- admin user maintenance
- admin reference maintenance
- browser navigation coverage

### Negative
- login blank/invalid validations
- report request validations
- access control validations

## Test Data Requirements
Update `test-data/testData.js` with real environment values before execution:
- standard user
- admin user
- valid/invalid account ids
- valid/invalid card numbers
- pending authorization id
- transaction creation data
- admin reference and user maintenance data

## Execution
```bash
npm install
npx playwright install
npm test
```

### Run specific suites
```bash
npm run test:smoke
npm run test:regression
npm run test:negative
```

## Notes
- Some selectors may need minor alignment with live DOM if templates differ slightly.
- The framework intentionally avoids inventing unsupported flows.
- Stable `data-testid` attributes are recommended for long-term reliability.
