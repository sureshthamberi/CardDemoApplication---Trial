const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const data = require('../../test-data/testData');

test.describe('Smoke - Authentication', () => {
  test('standard user can login and logout', async ({ page }) => {
    const login = new LoginPage(page);
    const menu = new MainMenuPage(page);

    await login.loginAsStandard(data.users.standard);
    await menu.assertLoaded();
    await menu.signOut();
    await login.expectUrlContains('/auth/login');
  });

  test('admin user can login', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsAdmin(data.users.admin);
  });
});
