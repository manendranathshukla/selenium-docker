package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.utils.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleSearchTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(GoogleSearchTest.class.getName());

    @Test
    public void searchTest() {
        try {
            LOGGER.info("Starting Google search test");
            
            // Navigate to Google
            LOGGER.info("Navigating to Google");
            driver.get("https://www.google.com");
            
            // Accept cookies if the dialog appears (common in EU)
            try {
                LOGGER.info("Checking for cookie consent dialog");
                // Wait for the cookie dialog to appear with a shorter timeout
                wait.withTimeout(java.time.Duration.ofSeconds(5));
                
                // Try different selectors for the accept button
                String[] acceptButtonSelectors = {
                    "//button[contains(., 'Accept')]", 
                    "//button[contains(., 'I agree')]",
                    "//button[contains(., 'Accept all')]",
                    "//div[contains(text(), 'Accept all')]",
                    "//button[@id='L2AGLb']"
                };
                
                boolean accepted = false;
                for (String selector : acceptButtonSelectors) {
                    try {
                        WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(selector)));
                        acceptButton.click();
                        LOGGER.info("Accepted cookies using selector: " + selector);
                        accepted = true;
                        break;
                    } catch (Exception e) {
                        // Try next selector
                    }
                }
                
                if (!accepted) {
                    LOGGER.info("No cookie dialog found or could not interact with it");
                }
                
                // Reset wait timeout to default
                wait.withTimeout(java.time.Duration.ofSeconds(30));
                
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "Cookie dialog not found or already accepted", e);
            }
            
            // Find the search box
            LOGGER.info("Finding search box");
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
            
            // Enter search query
            LOGGER.info("Entering search query: Selenium WebDriver");
            searchBox.sendKeys("Selenium WebDriver");
            
            // Submit the form
            LOGGER.info("Submitting search query");
            searchBox.submit();
            
            // Wait for the results page to load
            LOGGER.info("Waiting for search results to load");
            waitForPageLoad();
            
            // Verify that the page title contains the search query
            String pageTitle = driver.getTitle();
            LOGGER.info("Page title: " + pageTitle);
            Assert.assertTrue(pageTitle.contains("Selenium WebDriver"), 
                    "Page title does not contain search query. Title: " + pageTitle);
            
            // Verify search results contain relevant information
            WebElement searchResults = driver.findElement(By.id("search"));
            String resultsText = searchResults.getText().toLowerCase();
            Assert.assertTrue(resultsText.contains("selenium") || resultsText.contains("webdriver"), 
                    "Search results do not contain relevant information");
            
            LOGGER.info("Google search test completed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Google search test", e);
            Assert.fail("Google search test failed: " + e.getMessage());
        }
    }
}