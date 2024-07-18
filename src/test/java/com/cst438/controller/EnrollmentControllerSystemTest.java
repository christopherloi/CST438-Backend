package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class EnrollmentControllerSystemTest {
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver-win64/chromedriver.exe";

    //public static final String CHROME_DRIVER_FILE_LOCATION =
    //        "~/chromedriver_macOS/chromedriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.


    // add selenium dependency to pom.xml

    // these tests assumes that test data does NOT contain any
    // sections for course cst499 in 2024 Spring term.

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        // set properties required by Chrome Driver
        System.setProperty(
                "webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        // start the driver
        driver = new ChromeDriver(ops);

        driver.get(URL);
        // must have a short wait to allow time for the page to download
        Thread.sleep(SLEEP_DURATION);

    }

    @AfterEach
    public void terminateDriver() {
        if (driver != null) {
            // quit driver
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void systemTestFinalGrade() throws Exception {
        // instructor enters final grade for all enrolled students in a section
        // reset the grade back to the original value

        // InstructorHome.js
        // Enter the year and semester
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showsections")).click();
        Thread.sleep(SLEEP_DURATION);

        // InstructorSectionsView.js
        // Locate section 10
        WebElement row8 = driver.findElement(By.xpath("//tr[td='10']"));
        // Click the enrollments button
        row8.findElement(By.id("enrollments")).click();
        Thread.sleep(SLEEP_DURATION);

        // EnrollmentsView.js
        // Enter a grade
        String originalGrade = driver.findElement(By.name("grade")).getAttribute("value");
        driver.findElement(By.name("grade")).clear();
        driver.findElement(By.name("grade")).sendKeys("A");
        driver.findElement(By.tagName("button")).click();
        Thread.sleep(SLEEP_DURATION);
        // Verify the confirmation message
        String message = driver.findElement(By.id("message")).getText();
        assertTrue(message.startsWith("Grades saved"));
        // Set the grade back to original value
        driver.findElement(By.name("grade")).sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        driver.findElement(By.name("grade")).sendKeys(originalGrade);
        driver.findElement(By.tagName("button")).click();
        Thread.sleep(SLEEP_DURATION);
        // Verify grade is set back to original value
        message = driver.findElement(By.id("message")).getText();
        assertTrue(message.startsWith("Grades saved"));
        String resetGrade = driver.findElement(By.name("grade")).getAttribute("value");
        assertEquals(originalGrade, resetGrade);
    }
}
