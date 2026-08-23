const { test, expect } = require('@playwright/test');
const LoginPage = require('../../pages/LoginPage');
const MainMenuPage = require('../../pages/MainMenuPage');
const AccountsPage = require('../../pages/AccountsPage');
const CardsPage = require('../../pages/CardsPage');
const testData = require('../../test-data/testData');

test.describe('Regression - Navigation and Browser History', () => {
  test('standard user can navigate using links and back-forward browser actions', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const menuPage = new MainMenuPage(page);
    const accountsPage = new AccountsPage(page);
    const cardsPage = new CardsPage(page);

    await loginPage.loginAsStandard(testData.users.standard);
    await menuPage.assertLoaded();

    await menuPage.openAccountsInquiry();
    await accountsPage.assertLoaded();

    await page.goBack();
    await menuPage.assertLoaded();

    await page.goForward();
    await accountsPage.assertLoaded();

    await page.goto('/menu');
    await menuPage.openCards();
    await cardsPage.assertLoaded();

    await page.goBack();
    await expect(page.getByRole('heading', { name: /main menu|menu/i })).toBeVisible();
  });
});
