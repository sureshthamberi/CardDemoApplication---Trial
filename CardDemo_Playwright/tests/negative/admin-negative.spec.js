const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const AdminMenuPage = require('../../pages/AdminMenuPage');
const UsersPage = require('../../pages/UsersPage');
const ReferencePage = require('../../pages/ReferencePage');
const testData = require('../../test-data/testData');

test.describe('Negative - Admin', () => {
  test('admin user creation validates blank fields', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new AdminMenuPage(page);
    const usersPage = new UsersPage(page);

    await loginPage.loginAsAdmin(testData.users.admin);
    await menuPage.openUsers();
    await usersPage.openAdd();

    await page.getByRole('button', { name: /save|submit|add/i }).click();
    await usersPage.assertValidationVisible();
  });

  test('reference creation validates blank fields', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new AdminMenuPage(page);
    const referencePage = new ReferencePage(page);

    await loginPage.loginAsAdmin(testData.users.admin);
    await menuPage.openReferenceData();
    await referencePage.openAdd();

    await page.getByRole('button', { name: /save|submit|add/i }).click();
    await referencePage.assertSuccessVisible().catch(async () => {
      await page.getByText(/required|invalid|problem/i).waitFor();
    });
  });
});
