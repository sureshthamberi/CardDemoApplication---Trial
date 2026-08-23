const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const { expectCommonAccessibility } = require('../../utils/a11y');
const testData = require('../../test-data/testData');

test.describe('Accessibility - Pending Auth', () => {
  test('pending auth page has accessible headings and actions', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.openPendingAuthorizations();

    await expectCommonAccessibility(page, /pending auth|pending authorizations/i);
  });
});
