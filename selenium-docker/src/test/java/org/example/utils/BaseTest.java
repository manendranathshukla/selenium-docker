package org.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());
    
    @BeforeMethod
    @Parameters(value = {"browser"})
    public void setupTest(@org.testng.annotations.Optional("chrome") String browser) {
        // Check if we're running in a Docker container
        boolean isRunningInDocker = System.getenv("IS_RUNNING_IN_DOCKER") != null;
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        
        LOGGER.info("Setting up WebDriver for browser: " + browser);
        LOGGER.info("Running in Docker: " + isRunningInDocker);
        LOGGER.info("Headless mode: " + isHeadless);
        
        try {
            if (isRunningInDocker) {
                // Running in Docker - use RemoteWebDriver to connect to Selenium Grid
                try {
                    // The hub URL should match the service name in docker-compose.yml
                    URL hubUrl = new URL("http://selenium-hub:4444/wd/hub");
                    
                    if ("chrome".equalsIgnoreCase(browser)) {
                        ChromeOptions options = new ChromeOptions();
                        options.addArguments("--no-sandbox");
                        options.addArguments("--disable-dev-shm-usage");
                        // Fix for Chrome compatibility issues
                        options.addArguments("--remote-allow-origins=*");
                        // Adding alternative approach to handle potential space issue
                        Map<String, Object> prefs = new HashMap<>();
                        prefs.put("profile.default_content_setting_values.notifications", 2);
                        options.setExperimentalOption("prefs", prefs);
                        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                        options.setExperimentalOption("useAutomationExtension", false);
                        if (isHeadless) {
                            options.addArguments("--headless=new");
                        }
                        driver = new RemoteWebDriver(hubUrl, options);
                    } else if ("firefox".equalsIgnoreCase(browser)) {
                        FirefoxOptions options = new FirefoxOptions();
                        if (isHeadless) {
                            options.addArguments("--headless=new");
                        }
                        driver = new RemoteWebDriver(hubUrl, options);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                // Running locally - use local WebDriver
                if ("chrome".equalsIgnoreCase(browser)) {
                    // Auto-detect Chrome version
                    // WebDriverManager.chromedriver().setup();
                    System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--remote-allow-origins=*");
                    // Adding preferences to handle potential Chrome compatibility issues
                    Map<String, Object> prefs = new HashMap<>();
                    prefs.put("profile.default_content_setting_values.notifications", 2);
                    options.setExperimentalOption("prefs", prefs);
                    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                    options.setExperimentalOption("useAutomationExtension", false);
                    if (isHeadless) {
                        options.addArguments("--headless=new");
                    }
                    driver = new ChromeDriver(options);
                } else if ("firefox".equalsIgnoreCase(browser)) {
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions options = new FirefoxOptions();
                    if (isHeadless) {
                        options.addArguments("--headless=new");
                    }
                    driver = new FirefoxDriver(options);
                }
            }
            
            // Configure driver settings
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            LOGGER.info("WebDriver setup completed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up WebDriver", e);
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage(), e);
        }
    }
    
    @AfterMethod
    public void tearDown() {
        LOGGER.info("Tearing down WebDriver");
        try {
            if (driver != null) {
                driver.quit();
                LOGGER.info("WebDriver quit successfully");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error quitting WebDriver", e);
        } finally {
            driver = null;
            wait = null;
        }
    }
    
    /**
     * Utility method to wait for page to load completely
     */
    protected void waitForPageLoad() {
        LOGGER.info("Waiting for page to load completely");
        try {
            wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            LOGGER.info("Page loaded successfully");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error waiting for page to load", e);
        }
    }
}