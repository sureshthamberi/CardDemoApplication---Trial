module.exports = {
  users: {
    standard: {
      userId: 'standard01',
      password: 'Password123'
    },
    admin: {
      userId: 'admin01',
      password: 'Password123'
    },
    invalid: {
      userId: 'baduser',
      password: 'badpass'
    }
  },

  accounts: {
    validAccountId: '12345678901',
    invalidAccountId: '99999999999'
  },

  cards: {
    validCardNumber: '4111111111111111',
    invalidCardNumber: '1234567890123456'
  },

  pendingAuth: {
    validAuthorizationId: '1001'
  },

  transactions: {
    validStartTransactionId: '',
    addTransaction: {
      accountId: '12345678901',
      cardNumber: '4111111111111111',
      categoryType: 'RETAIL',
      source: 'ONLINE',
      amount: '25.50',
      description: 'Playwright test transaction',
      originalDate: { day: '10', month: '06', year: '2026' },
      processDate: { day: '10', month: '06', year: '2026' },
      merchantName: 'PW Merchant',
      merchantCity: 'London',
      merchantId: 'M12345',
      merchantZip: 'SW1A1AA'
    }
  },

  reports: {
    customRange: {
      startDate: { day: '01', month: '06', year: '2026' },
      endDate: { day: '30', month: '06', year: '2026' }
    }
  },

  admin: {
    referenceType: {
      typeCode: 'PW01',
      description: 'Playwright Type'
    },
    updatedReferenceDescription: 'Playwright Type Updated',
    newUser: {
      firstName: 'Play',
      lastName: 'Writer',
      userId: 'pwuser01',
      password: 'Secret123',
      userType: 'STANDARD'
    },
    updatedUser: {
      firstName: 'PlayUpdated',
      lastName: 'WriterUpdated',
      password: 'Secret456',
      userType: 'ADMIN'
    }
  }
};
