const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const AdminMenuPage = require('../../pages/AdminMenuPage');
const ReferencePage = require('../../pages/ReferencePage');
const UsersPage = require('../../pages/UsersPage');
const testData = require('../../test-data/testData');

test.describe('Smoke - Admin User', () => {
  test('admin user can access admin modules', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new AdminMenuPage(page);
    const referencePage = new ReferencePage(page);
    const usersPage = new UsersPage(page);

    await loginPage.loginAsAdmin(testData.users.admin);
    await menuPage.assertLoaded();

    await menuPage.openReferenceData();
    await referencePage.assertLoaded();

    await page.goBack();

    await menuPage.openUsers();
    await usersPage.assertLoaded();
  });
});
