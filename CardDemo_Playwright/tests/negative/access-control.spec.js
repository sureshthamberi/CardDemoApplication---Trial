const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const data = require('../../test-data/testData');

test.describe('Negative - Access control', () => {
  test('unauthenticated user redirected to login for protected route', async ({ page }) => {
    await page.goto('/accounts/inquiry');
    await expect(page).toHaveURL(/\/auth\/login/);
  });

  test('standard user cannot access admin users page', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);

    await page.goto('/admin/users');
    await expect(page.getByText(/access denied|do not have permission/i)).toBeVisible();
  });

  test('standard user cannot access reference data page', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAsStandard(data.users.standard);

    await page.goto('/admin/transaction-types');
    await expect(page.getByText(/access denied|do not have permission/i)).toBeVisible();
  });
});
