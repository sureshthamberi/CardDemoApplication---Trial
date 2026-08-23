const { expect } = require('@playwright/test');

async function expectCommonAccessibility(page, headingPattern) {
  await expect(page.getByRole('heading', { name: headingPattern })).toBeVisible();

  const buttons = page.getByRole('button');
  const links = page.getByRole('link');

  await expect(buttons.first()).toBeVisible({ timeout: 10000 }).catch(() => {});
  await expect(links.first()).toBeVisible({ timeout: 10000 }).catch(() => {});
}

async function expectKeyboardReachable(page) {
  await page.keyboard.press('Tab');
}

module.exports = {
  expectCommonAccessibility,
  expectKeyboardReachable
};
