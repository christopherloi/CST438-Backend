package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentControllerSystemTest {
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
    public void systemTestAddAssignment() throws Exception {
        // instructor adds a new assignment successfully
        // verify the section was added
        // delete the section
        // verify the section was deleted

        // InstructorHome.js
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showsections")).click();
        Thread.sleep(SLEEP_DURATION);

        // InstructorSectionsView.js
        driver.findElement(By.id("assignments")).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentsView.js
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        buttons.get(6).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentAdd.js
        driver.findElement(By.name("title")).sendKeys("Test Assignment");
        driver.findElement(By.name("dueDate")).sendKeys("2024-05-16");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentsView.js
        String message = driver.findElement(By.id("statusMessage")).getText();
        assertTrue(message.startsWith("Assignment created"));
        // Delete the new assignment
        WebElement testAssignment = driver.findElement(By.xpath("//tr[td='Test Assignment']"));
        buttons = testAssignment.findElements(By.tagName("button"));
        // delete is the second button;
        buttons.get(2).click();
        Thread.sleep(SLEEP_DURATION);
        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);
   }
}
