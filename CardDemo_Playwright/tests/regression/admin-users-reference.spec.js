const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const ReferencePage = require('../../pages/ReferencePage');
const UsersPage = require('../../pages/UsersPage');
const data = require('../../test-data/testData');

test.describe('Regression - Admin users and reference maintenance', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsAdmin(data.users.admin);
  });

  test('reference data add edit delete', async ({ page }) => {
    const reference = new ReferencePage(page);

    await reference.openAdd();
    await reference.addType(
      data.admin.referenceType.typeCode,
      data.admin.referenceType.description
    );

    await reference.openEdit(data.admin.referenceType.typeCode);
    await reference.updateDescription(data.admin.updatedReferenceDescription);

    await reference.openDelete(data.admin.referenceType.typeCode);
    await reference.confirmDelete();
  });

  test('user add edit delete', async ({ page }) => {
    const users = new UsersPage(page);

    await users.openAdd();
    await users.addUser(data.admin.newUser);

    await users.openEdit(data.admin.newUser.userId);
    await users.editUser(data.admin.updatedUser);

    await users.openDelete(data.admin.newUser.userId);
    await users.confirmDelete();
  });
});
