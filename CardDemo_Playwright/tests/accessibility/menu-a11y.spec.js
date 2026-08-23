const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const { expectCommonAccessibility } = require('../../utils/a11y');
const testData = require('../../test-data/testData');

test.describe('Accessibility - Menu', () => {
  test('main menu has accessible navigation items', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAsStandard(testData.users.standard);

    await expectCommonAccessibility(page, /main menu|menu/i);
    await expect(page.getByRole('link', { name: /accounts|account inquiry/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /transactions/i })).toBeVisible();
  });
});
