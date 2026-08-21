const { test } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const data = require('../../test-data/testData');

test.describe('Negative - Login validation', () => {
  test('blank login shows required errors', async ({ page }) => {
    const login = new LoginPage(page);
    await login.open();
    await login.clickButton(/sign in/i);
    await login.assertLoginValidationErrors();
  });

  test('invalid login shows credentials error', async ({ page }) => {
    const login = new LoginPage(page);
    await login.open();
    await login.login(data.users.invalid.userId, data.users.invalid.password);
    await login.assertInvalidCredentialError();
  });
});
