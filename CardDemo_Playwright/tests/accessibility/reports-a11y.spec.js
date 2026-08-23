const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const { expectCommonAccessibility } = require('../../utils/a11y');
const testData = require('../../test-data/testData');

test.describe('Accessibility - Reports', () => {
  test('reports page has accessible radio options and submit action', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.openReports();

    await expectCommonAccessibility(page, /reports|report request/i);
  });
});
