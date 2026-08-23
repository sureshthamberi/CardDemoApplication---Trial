const { test, expect } = require('@playwright/test');
const { expectCommonAccessibility, expectKeyboardReachable } = require('../../utils/a11y');

test.describe('Accessibility - Auth', () => {
  test('login page exposes accessible controls', async ({ page }) => {
    await page.goto('/auth/login');

    await expectCommonAccessibility(page, /sign in|login/i);
    await expect(page.getByRole('textbox', { name: /user id/i })).toBeVisible();
    await expect(page.getByRole('button', { name: /sign in|login/i })).toBeVisible();

    await expectKeyboardReachable(page);
  });
});
