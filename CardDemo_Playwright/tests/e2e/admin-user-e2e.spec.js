const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const AdminMenuPage = require('../../pages/AdminMenuPage');
const ReferencePage = require('../../pages/ReferencePage');
const UsersPage = require('../../pages/UsersPage');
const testData = require('../../test-data/testData');

test.describe('E2E - Admin User', () => {
  test('admin user can complete reference and user administration journey', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new AdminMenuPage(page);
    const referencePage = new ReferencePage(page);
    const usersPage = new UsersPage(page);

    await loginPage.loginAsAdmin(testData.users.admin);
    await menuPage.assertLoaded();

    await menuPage.openReferenceData();
    await referencePage.openAdd();
    await referencePage.addType(
      testData.admin.referenceType.typeCode,
      testData.admin.referenceType.description
    );
    await referencePage.assertSuccessVisible();

    await referencePage.openEdit(testData.admin.referenceType.typeCode);
    await referencePage.updateDescription(testData.admin.updatedReferenceDescription);
    await referencePage.assertSuccessVisible();

    await page.goto('/menu');
    await menuPage.openUsers();
    await usersPage.openAdd();
    await usersPage.addUser(testData.admin.newUser);
    await usersPage.assertSuccessVisible();

    await usersPage.openEdit(testData.admin.newUser.userId);
    await usersPage.editUser(testData.admin.updatedUser);
    await usersPage.assertSuccessVisible();

    await usersPage.openDelete(testData.admin.newUser.userId);
    await usersPage.confirmDelete();
    await usersPage.assertSuccessVisible();

    await page.goto('/menu');
    await menuPage.openReferenceData();
    await referencePage.openDelete(testData.admin.referenceType.typeCode);
    await referencePage.confirmDelete();
    await referencePage.assertSuccessVisible();

    await menuPage.signOut();
    await expect(page).toHaveURL(/auth/login/);
  });
});
