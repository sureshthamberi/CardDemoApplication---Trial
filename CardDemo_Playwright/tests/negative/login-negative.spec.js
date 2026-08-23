const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const testData = require('../../test-data/testData');

test.describe('Negative - Login', () => {
  test('user cannot sign in with blank credentials', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.submitBlank();
    await loginPage.assertValidationVisible();
  });

  test('user cannot sign in with invalid credentials', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.open();
    await loginPage.login(testData.users.invalid.userId, testData.users.invalid.password);
    await loginPage.assertInvalidLoginError();
  });
});
