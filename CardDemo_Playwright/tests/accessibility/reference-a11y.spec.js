const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const AdminMenuPage = require('../../pages/AdminMenuPage');
const { expectCommonAccessibility } = require('../../utils/a11y');
const testData = require('../../test-data/testData');

test.describe('Accessibility - Reference', () => {
  test('reference page has accessible admin controls', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new AdminMenuPage(page);

    await loginPage.loginAsAdmin(testData.users.admin);
    await menuPage.openReferenceData();

    await expectCommonAccessibility(page, /transaction type|reference/i);
  });
});
