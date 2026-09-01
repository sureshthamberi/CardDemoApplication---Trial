const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const testData = require('../../test-data/testData');

test.describe('Negative - Access Control', () => {
  test('unauthenticated user cannot access protected standard page', async ({ page }) => {
    await page.goto('/accounts');
    await expect(page).toHaveURL(/auth\/login|login/);
  });

  test('unauthenticated user cannot access protected admin page', async ({ page }) => {
    await page.goto('/admin/users');
    await expect(page).toHaveURL(/auth\/login|login/);
  });

  test('standard user should not access admin users page', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAsStandard(testData.users.standard);

    await page.goto('/admin/users');
    
    //await expect(page).toHaveURL(/auth\/login|login|menu/);
    await expect(page.getByRole('heading', { name: 'Access denied' })).toBeVisible();
  });

  test('standard user should not access admin reference page', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAsStandard(testData.users.standard);

    await page.goto('/admin/transaction-types');
    //await expect(page).toHaveURL(/auth\/login|login|menu/);
    await expect(page.getByRole('heading', { name: 'Access denied' })).toBeVisible();
  });
});
