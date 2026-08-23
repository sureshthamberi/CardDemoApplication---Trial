const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const testData = require('../../test-data/testData');

test.describe('Negative - Access Control', () => {
  test('unauthenticated user cannot access protected standard page', async ({ page }) => {
    await page.goto('/accounts');
    await expect(page).toHaveURL(/auth/login|login/);
  });

  test('unauthenticated user cannot access protected admin page', async ({ page }) => {
    await page.goto('/users');
    await expect(page).toHaveURL(/auth/login|login/);
  });

  test('standard user should not access admin users page', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAsStandard(testData.users.standard);

    await page.goto('/users');
    await expect(page.getByText(/not authorized|forbidden|access denied|login/i)).toBeVisible();
  });

  test('standard user should not access admin reference page', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAsStandard(testData.users.standard);

    await page.goto('/reference');
    await expect(page.getByText(/not authorized|forbidden|access denied|login/i)).toBeVisible();
  });
});
