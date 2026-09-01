# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regression\transactions-reports-pendingauth.spec.js >> Regression - Transactions, Reports, Pending Auth >> user can access transactions, submit reports and view pending auth
- Location: tests\regression\transactions-reports-pendingauth.spec.js:10:3

# Error details

```
Error: expect(locator).toContainText(expected) failed

Locator: locator('main')
Timeout: 15000ms
Expected pattern: /submitted|requested|success|report generated/i
Received string:  "··················
Reporting
Report Request···············
      There is a problem································································
            Start date is required·····················································
            End date is required·····································································································
    Report Type·············
    Select the reporting period······························································
        Monthly·····················
        Current calendar month············································································
        Yearly·····················
        Current calendar year··········································
    or····················································
        Custom date range··········································································
      Start Date·······················
      For example, 1 6 2026·························································
            Day···································································································
            Month···································································································
            Year····································································································································
      End Date·······················
      For example, 30 6 2026·························································
            Day···································································································
            Month···································································································
            Year······································································································································································
    Confirmation·············
    Confirm you want to submit this report request······························································
        Yes, submit this report············································································
        No···········································································
  Submit Request··
        Cancel·····················
  "

Call log:
  - Expect "toContainText" with timeout 15000ms
  - waiting for locator('main')
    33 × locator resolved to <main role="main" id="main-content" class="govuk-main-wrapper">…</main>
       - unexpected value "

    
    

    

Reporting
Report Request




  
  
    
      There is a problem
    
    
      
      
        
        
          
          
            Start date is required
          
          
        
          
          
            End date is required
          
          
        
        
      
    
  





  
    

      

      





  
  
  
  
    Report Type
  
  
  

  
    Select the reporting period
  


  
    
    
      
  
    
    
    
    
      
      
        Monthly
      
      
      
        Current calendar month
      
      
    
    
  
    
      
  
    
    
    
    
      
      
        Yearly
      
      
      
        Current calendar year
      
      
    
    
  
    
      
  
    or
  
    
      
  
    
    
    
    
      
      
        Custom date range
      
      
    
    
    
      
          




  


  
    
    
    
      Start Date
    
    
    
  
    
      For example, 1 6 2026
    
  
  
    
      
      
      
        
          
            Day
          
        
        
          
        
        
      
      
      
        
          
            Month
          
        
        
          
        
        
      
      
      
        
          
            Year
          
        
        
          
        
        
      
      
      
    
  
  




          




  


  
    
    
    
      End Date
    
    
    
  
    
      For example, 30 6 2026
    
  
  
    
      
      
      
        
          
            Day
          
        
        
          
        
        
      
      
      
        
          
            Month
          
        
        
          
        
        
      
      
      
        
          
            Year
          
        
        
          
        
        
      
      
      
    
  
  



        
    
    
  
    
    
  






      

      





  
  
  
  
    Confirmation
  
  
  

  
    Confirm you want to submit this report request
  


  
    
    
      
  
    
    
    
    
      
      
        Yes, submit this report
      
      
    
    
  
    
      
  
    
    
    
    
      
      
        No
      
      
    
    
  
    
    
  






      
        
  
    
  


  Submit Request


        Cancel
      

    
  





  "

```

```yaml
- main:
  - text: Reporting
  - heading "Report Request" [level=1]
  - alert:
    - heading "There is a problem" [level=2]
    - list:
      - listitem:
        - link "Start date is required":
          - /url: "#startDate-day"
      - listitem:
        - link "End date is required":
          - /url: "#endDate-day"
  - group "Report Type":
    - text: Report Type Select the reporting period
    - radio "Monthly"
    - text: Monthly Current calendar month
    - radio "Yearly"
    - text: Yearly Current calendar year or
    - radio "Custom date range" [checked]
    - text: Custom date range
    - group "Start Date":
      - text: Start Date For example, 1 6 2026 Day
      - textbox "Day"
      - text: Month
      - textbox "Month"
      - text: Year
      - textbox "Year"
    - group "End Date":
      - text: End Date For example, 30 6 2026 Day
      - textbox "Day"
      - text: Month
      - textbox "Month"
      - text: Year
      - textbox "Year"
  - separator
  - group "Confirmation":
    - text: Confirmation Confirm you want to submit this report request
    - radio "Yes, submit this report" [checked]
    - text: Yes, submit this report
    - radio "No"
    - text: "No"
  - button "Submit Request"
  - link "Cancel":
    - /url: /menu
```

# Test source

```ts
  1  | const { expect } = require('@playwright/test');
  2  | const BasePage = require('./BasePage');
  3  | 
  4  | class ReportsPage extends BasePage {
  5  |   async assertLoaded() {
  6  |     await this.expectHeading(/reports|report request/i);
  7  |   }
  8  | 
  9  |   async submitMonthly() {
  10 |     const monthlyRadio = this.page.getByRole('radio', { name: /monthly/i });
  11 |     if (await monthlyRadio.count()) {
  12 |       await monthlyRadio.check();
  13 |     }
  14 |     await this.page.getByRole('radio', { name: /yes, submit this report/i }).check();
  15 |     await this.page.getByRole('button', { name: /submit|continue|request/i }).click();
  16 |   }
  17 | 
  18 |   async submitYearly() {
  19 |     const yearlyRadio = this.page.getByRole('radio', { name: /yearly/i });
  20 |     if (await yearlyRadio.count()) {
  21 |       await yearlyRadio.check();
  22 |     }
  23 |     await this.page.getByRole('radio', { name: /yes, submit this report/i }).check();
  24 |     await this.page.getByRole('button', { name: /submit|continue|request/i }).click();
  25 |   }
  26 | 
  27 |   async submitCustomRange(range) {
  28 |     const customRadio = this.page.getByRole('radio', { name: /custom/i });
  29 |     if (await customRadio.count()) {
  30 |       await customRadio.check();
  31 |     }
  32 | 
  33 |     const textboxes = this.page.getByRole('textbox');
  34 |     const dayCount = await textboxes.count();
  35 | 
  36 |     if (dayCount >= 6) {
  37 |       await textboxes.nth(0).fill(range.startDate.day);
  38 |       await textboxes.nth(1).fill(range.startDate.month);
  39 |       await textboxes.nth(2).fill(range.startDate.year);
  40 |       await textboxes.nth(3).fill(range.endDate.day);
  41 |       await textboxes.nth(4).fill(range.endDate.month);
  42 |       await textboxes.nth(5).fill(range.endDate.year);
  43 |     }
  44 | 
  45 |     await this.page.getByRole('radio', { name: /yes, submit this report/i }).check();
  46 |     await this.page.getByRole('button', { name: /submit|continue|request/i }).click();
  47 |   }
  48 | 
  49 |   async assertSubmitted() {
> 50 |     await expect(this.page.locator('main')).toContainText(/submitted|requested|success|report generated/i);
     |                                             ^ Error: expect(locator).toContainText(expected) failed
  51 |   }
  52 | 
  53 |   async assertValidationVisible() {
  54 |     await expect(this.page.locator('.govuk-error-summary, .govuk-error-message').first()).toBeVisible();
  55 |   }
  56 | }
  57 | 
  58 | module.exports = ReportsPage;
  59 | 
```