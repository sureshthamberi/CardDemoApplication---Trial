const { test, expect } = require('@playwright/test');

test.describe('UI/UX - Form Validation', () => {
  test('login validation feedback is visible to the user', async ({ page }) => {
    await page.goto('/auth/login');
    await page.getByRole('button', { name: /sign in|login/i }).click();

    await expect(page.getByText(/required|problem|enter/i)).toBeVisible();
  });
});
