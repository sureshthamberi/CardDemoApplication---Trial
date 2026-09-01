const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const testData = require('../../test-data/testData');

test.describe('Smoke - Auth', () => {
  test('standard user can sign in and sign out', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.assertLoaded();

    await menuPage.signOut();
    await expect(page).toHaveURL(/auth\/login/);
  });

  test('admin user can sign in', async ({ page }) => {
    const loginPage = new LoginPage(page);

    await loginPage.loginAsAdmin(testData.users.admin);
    await expect(page).toHaveURL(/menu|admin|home/);
  });
});
