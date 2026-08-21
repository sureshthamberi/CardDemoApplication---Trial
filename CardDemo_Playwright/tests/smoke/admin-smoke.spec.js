const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const ReferencePage = require('../../pages/ReferencePage');
const UsersPage = require('../../pages/UsersPage');
const data = require('../../test-data/testData');

test.describe('Smoke - Admin pages', () => {
  test.beforeEach(async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsAdmin(data.users.admin);
  });

  test('admin can access users and reference pages', async ({ page }) => {
    const users = new UsersPage(page);
    const reference = new ReferencePage(page);

    await users.openList();
    await reference.openList();
  });
});
