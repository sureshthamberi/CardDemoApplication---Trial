const { expect } = require('@playwright/test');

async function verifyBackForward(page, expectedBackHeading, expectedForwardHeading) {
  await page.goBack();
  await expect(page.getByRole('heading', { name: expectedBackHeading })).toBeVisible();

  await page.goForward();
  await expect(page.getByRole('heading', { name: expectedForwardHeading })).toBeVisible();
}

module.exports = {
  verifyBackForward
};
