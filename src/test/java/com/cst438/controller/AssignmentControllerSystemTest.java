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
        // verify the assignment was added
        // delete the assignment
        // verify the assignment was deleted

        // InstructorHome.js
        // Enter the year and semester
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showsections")).click();
        Thread.sleep(SLEEP_DURATION);

        // InstructorSectionsView.js
        // Locate section 8
        WebElement row8 = driver.findElement(By.xpath("//tr[td='8']"));
        // Click the assignments button
        row8.findElement(By.id("assignments")).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentsView.js
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        // Click the last button on the page which is "Add Assignments"
        buttons.get(buttons.size() - 1).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentAdd.js
        // Enter the information for the new assignment to be added
        driver.findElement(By.name("title")).sendKeys("Test Assignment");
        driver.findElement(By.name("dueDate")).sendKeys("2024-05-16");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentsView.js
        // Verify the assignment was added by finding the status message
        String message = driver.findElement(By.id("statusMessage")).getText();
        assertTrue(message.startsWith("Assignment created"));
        // Delete the new assignment
        WebElement testAssignment = driver.findElement(By.xpath("//tr[td='Test Assignment']"));
        buttons = testAssignment.findElements(By.tagName("button"));
        // delete is the third button;
        buttons.get(2).click();
        Thread.sleep(SLEEP_DURATION);
        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        // verify the deleted assignment is gone
        assertThrows(NoSuchElementException.class, () ->
                driver.findElement(By.xpath("//tr[td='Test Assignment']")));
   }

    @Test
    public void systemTestGradeAssignment() throws Exception {
        // instructor adds a new assignment successfully
        // verify the assignment was added
        // delete the assignment
        // verify the assignment was deleted

        // InstructorHome.js
        // Enter the year and semester
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showsections")).click();
        Thread.sleep(SLEEP_DURATION);

        // InstructorSectionsView.js
        // Locate section 8
        WebElement row8 = driver.findElement(By.xpath("//tr[td='8']"));
        // Click the assignments button
        row8.findElement(By.id("assignments")).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentsView.js
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        // Click the first button on the page which is "GRADE"
        buttons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        // AssignmentGrade.js
        // Enter the information for the new Grade to be added
        String originalScore = driver.findElement(By.name("score")).getAttribute("value");
        WebElement scoreInput = driver.findElement(By.name("score"));
        scoreInput.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        scoreInput.sendKeys("85");
        Thread.sleep(SLEEP_DURATION);

        // Click on the Save button
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        // Verify that a PUT request was sent to ${SERVER_URL}/grades (assuming successful request)
        // Example using Selenium to verify a success message
        WebElement successMessage = driver.findElement(By.xpath("//h4[contains(text(), 'Grades saved')]"));
        assertTrue(successMessage.isDisplayed());

        // Verify that the updated score is reflected in the UI after saving
        // Assuming you have a way to identify the updated score element in the table
        WebElement updatedScore = driver.findElement(By.xpath("//input[@name='score']"));
        assertEquals("85", updatedScore.getAttribute("value"));

        scoreInput = driver.findElement(By.name("score"));
        scoreInput.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        scoreInput.sendKeys(originalScore);
        Thread.sleep(SLEEP_DURATION);

        // Click on the Save button
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        // Verify that a PUT request was sent to ${SERVER_URL}/grades (assuming successful request)
        // Example using Selenium to verify a success message
        successMessage = driver.findElement(By.xpath("//h4[contains(text(), 'Grades saved')]"));
        assertTrue(successMessage.isDisplayed());

        // Verify that the updated score is reflected in the UI after saving
        // Assuming you have a way to identify the updated score element in the table
        updatedScore = driver.findElement(By.xpath("//input[@name='score']"));
        assertEquals(originalScore, updatedScore.getAttribute("value"));

        // Close the dialog or navigate back to the assignments view
        driver.findElement(By.id("close")).click();
        Thread.sleep(SLEEP_DURATION);

    }

}
