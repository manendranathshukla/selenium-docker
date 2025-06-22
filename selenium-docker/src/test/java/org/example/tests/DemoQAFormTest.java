package org.example.tests;

import org.example.utils.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DemoQAFormTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(DemoQAFormTest.class.getName());

    @Test
    public void fillFormTest() {
        try {
            LOGGER.info("Starting DemoQA form test");
            
            // Navigate to DemoQA Practice Form
            LOGGER.info("Navigating to DemoQA Practice Form");
            driver.get("https://demoqa.com/automation-practice-form");
            waitForPageLoad();
            
            // Handle any ads or overlays that might interfere with the test
            try {
                LOGGER.info("Checking for ads or overlays");
                WebElement adCloseButton = driver.findElement(By.id("close-fixedban"));
                if (adCloseButton.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", adCloseButton);
                    LOGGER.info("Closed ad banner");
                }
            } catch (Exception e) {
                LOGGER.info("No ad banner found or could not close it");
            }
            
            // Fill out the form
            // First Name
            LOGGER.info("Entering first name");
            WebElement firstName = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstName")));
            firstName.sendKeys("John");
            
            // Last Name
            LOGGER.info("Entering last name");
            WebElement lastName = driver.findElement(By.id("lastName"));
            lastName.sendKeys("Doe");
            
            // Email
            LOGGER.info("Entering email");
            WebElement email = driver.findElement(By.id("userEmail"));
            email.sendKeys("john.doe@example.com");
            
            // Gender (select Male)
            LOGGER.info("Selecting gender");
            WebElement maleRadio = driver.findElement(By.xpath("//label[text()='Male']"));
            scrollToAndClick(maleRadio);
            
            // Mobile Number
            LOGGER.info("Entering mobile number");
            WebElement mobileNumber = driver.findElement(By.id("userNumber"));
            mobileNumber.sendKeys("1234567890");
            
            // Date of Birth
            LOGGER.info("Setting date of birth");
            WebElement dateOfBirthInput = driver.findElement(By.id("dateOfBirthInput"));
            scrollToAndClick(dateOfBirthInput);
            
            // Select month (May)
            LOGGER.info("Selecting month");
            WebElement monthSelect = wait.until(ExpectedConditions.elementToBeClickable(By.className("react-datepicker__month-select")));
            Select monthDropdown = new Select(monthSelect);
            monthDropdown.selectByVisibleText("May");
            
            // Select year (1990)
            LOGGER.info("Selecting year");
            WebElement yearSelect = driver.findElement(By.className("react-datepicker__year-select"));
            Select yearDropdown = new Select(yearSelect);
            yearDropdown.selectByVisibleText("1990");
            
            // Select day (15)
            LOGGER.info("Selecting day");
            WebElement daySelect = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'react-datepicker__day--015')]"))); 
            daySelect.click();
            
            // Subjects
            LOGGER.info("Entering subjects");
            WebElement subjectsInput = driver.findElement(By.id("subjectsInput"));
            subjectsInput.sendKeys("Computer Science");
            subjectsInput.sendKeys("\n");
            
            // Hobbies (select Sports)
            LOGGER.info("Selecting hobbies");
            WebElement sportsCheckbox = driver.findElement(By.xpath("//label[text()='Sports']"));
            scrollToAndClick(sportsCheckbox);
            
            // Current Address
            LOGGER.info("Entering current address");
            WebElement currentAddress = driver.findElement(By.id("currentAddress"));
            currentAddress.sendKeys("123 Test Street, Test City, 12345");
            
            // Submit the form
            LOGGER.info("Submitting the form");
            WebElement submitButton = driver.findElement(By.id("submit"));
            scrollToAndClick(submitButton);
            
            // Verify submission
            LOGGER.info("Verifying form submission");
            WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("example-modal-sizes-title-lg")));
            Assert.assertEquals(modalTitle.getText(), "Thanks for submitting the form", 
                    "Form submission confirmation not displayed");
            
            // Verify submitted data
            LOGGER.info("Verifying submitted data");
            WebElement modalBody = driver.findElement(By.className("modal-body"));
            String modalText = modalBody.getText();
            Assert.assertTrue(modalText.contains("John Doe"), "Name verification failed");
            Assert.assertTrue(modalText.contains("john.doe@example.com"), "Email verification failed");
            Assert.assertTrue(modalText.contains("Male"), "Gender verification failed");
            Assert.assertTrue(modalText.contains("1234567890"), "Mobile verification failed");
            Assert.assertTrue(modalText.contains("15 May,1990"), "Date of birth verification failed");
            
            LOGGER.info("DemoQA form test completed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in DemoQA form test", e);
            Assert.fail("DemoQA form test failed: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to scroll to an element and click it using JavaScript
     * This is more reliable than regular clicks in newer Chrome versions
     */
    private void scrollToAndClick(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error clicking element", e);
            throw e;
        }
    }
}