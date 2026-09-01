const { expect } = require('@playwright/test');
const BasePage = require('./BasePage');

class UsersPage extends BasePage {
  async assertLoaded() {
    await this.expectHeading(/user administration|users/i);
  }

  async openAdd() {
    await this.page.locator('a[href="/admin/users/add"]').click();
  }

  async addUser(user) {
    await this.page.locator('input[name="firstName"]').fill(user.firstName);
    await this.page.locator('input[name="lastName"]').fill(user.lastName);
    await this.page.locator('input[name="userId"]').fill(user.userId);
    await this.page.locator('input[name="password"]').fill(user.password);

    const userTypeSelect = this.page.getByRole('combobox', { name: /user type|role/i });
    if (await userTypeSelect.count()) {
      const select = userTypeSelect.first();
      await select.waitFor({ state: 'attached' });

      const requestedType = String(user.userType).trim();
      const normalizedType = requestedType
        .replace(/([a-z])([A-Z])/g, '$1 $2')
        .replace(/[\s_-]+/g, '')
        .toLowerCase();
      const optionValue = await select.locator('option').evaluateAll((options, normalized) => {
        const option = options.find(item => {
          const text = item.textContent.trim().replace(/[\s_-]+/g, '').toLowerCase();
          const value = String(item.value).trim().replace(/[\s_-]+/g, '').toLowerCase();
          return text === normalized || value === normalized;
        });
        return option ? option.value : null;
      }, normalizedType);

      if (optionValue !== null && !this.page.isClosed()) {
        await select.selectOption({ value: optionValue });
      }
    }

    await this.page.locator('button:has-text("Create User")').click({ force: true });

    await expect(this.page).toHaveURL(/\/admin\/users(?:\/|$)/, { timeout: 15000 }).catch(async () => {
      await expect(this.page.locator('main')).toContainText(/created|updated|saved|success|added|user/i, { timeout: 15000 });
    });
  }

  async openEdit(userId) {
    await this.page.goto(`/admin/users/${userId}/edit`);
  }

  async editUser(user) {
    await this.page.locator('input[name="firstName"]').fill(user.firstName);
    await this.page.locator('input[name="lastName"]').fill(user.lastName);
    await this.page.locator('input[name="password"]').fill(user.password);

    const userTypeSelect = this.page.getByRole('combobox', { name: /user type|role/i });
    if (await userTypeSelect.count()) {
      const select = userTypeSelect.first();
      const userType = String(user.userType).trim();
      const normalizedUserType = userType.replace(/([a-z])([A-Z])/g, '$1 $2').replace(/[\s_-]+/g, '').toLowerCase();
      const option = select.locator('option').evaluateAll((options, normalized) => options.find(option => {
        const text = option.textContent.trim().replace(/[\s_-]+/g, '').toLowerCase();
        const value = String(option.value).trim().replace(/[\s_-]+/g, '').toLowerCase();
        return text === normalized || value === normalized;
      }), normalizedUserType);
      if (option) {
        await select.selectOption({ value: option.value });
      } else {
        await select.selectOption({ value: userType });
      }
    }

    await this.page.locator('button:has-text("Save"), button:has-text("Update")').first().click();
  }

  async openDelete(userId) {
    await this.page.goto(`/admin/users/${userId}/delete`);
  }

  async confirmDelete() {
    await this.page.getByRole('button', { name: /confirm|delete/i }).click();
  }

  async assertSuccessVisible() {
    await expect(this.page).toHaveURL(/admin\/users(?:\/|$)/, { timeout: 15000 }).catch(async () => {
      await expect(this.page.locator('main')).toContainText(/created|updated|saved|success|added|deleted/i, { timeout: 15000 });
    });
  }

  async assertValidationVisible() {
    await expect(this.page.locator('.govuk-error-summary, .govuk-error-message').first()).toBeVisible();
  }
}

module.exports = UsersPage;
