package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class EnrollmentControllerSystemTest {

    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver-win64/chromedriver.exe";

    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

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

    @Test //userType = 'STUDENT'
    public void studentEnrollsInSection() throws Exception {

        // navigate to view transcript
        driver.findElement(By.linkText("View Transcript")).click();
        Thread.sleep(SLEEP_DURATION);
        Thread.sleep(SLEEP_DURATION);

        // navigate to Enroll in class
        driver.findElement(By.linkText("Enroll in a class")).click();
        Thread.sleep(SLEEP_DURATION);

        //Enroll in class
        driver.findElement(By.cssSelector(".MuiButtonBase-root")).click();
        Thread.sleep(SLEEP_DURATION);
        driver.findElement(By.cssSelector(".react-confirm-alert-button-group > button:nth-child(1)")).click();
        Thread.sleep(SLEEP_DURATION);

        // Assertion to check if the section was added successfully
        String expectedCourseNotification = "section added";
        boolean isCoursePresent = driver.getPageSource().contains(expectedCourseNotification);
        assertTrue(isCoursePresent, "The course was added to the schedule.");

        // navigate to view transcript to confirm adding class
        driver.findElement(By.linkText("View Transcript")).click();
        Thread.sleep(SLEEP_DURATION);
        Thread.sleep(SLEEP_DURATION);

        //click link to navigate to class schedule
        driver.findElement(By.linkText("VIew Class Schedule")).click();
        Thread.sleep(SLEEP_DURATION);

        // fill in data
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Fall");
        driver.findElement(By.cssSelector("button")).click();
        Thread.sleep(SLEEP_DURATION);
        driver.findElement(By.cssSelector(".MuiButtonBase-root")).click();
        driver.findElement(By.cssSelector(".react-confirm-alert-button-group > button:nth-child(1)")).click();
        Thread.sleep(SLEEP_DURATION);

        // Assertion to check if the section was dropped successfully
        expectedCourseNotification = "course dropped";
        isCoursePresent = driver.getPageSource().contains(expectedCourseNotification);
        assertTrue(isCoursePresent, "The course was added to the schedule.");

        //view transcript to confirm removal
        driver.findElement(By.linkText("View Transcript")).click();
        Thread.sleep(SLEEP_DURATION);
    }

    @Test //userType = 'INSTRUCTOR'
    public void testEnterGrades() throws Exception {

        //Enter year & date to see sections
        Thread.sleep(SLEEP_DURATION);
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showsections")).click();
        Thread.sleep(SLEEP_DURATION);

        //Click a section to view assignments
        driver.findElement(By.id("assignments")).click();
        Thread.sleep(SLEEP_DURATION);

        //Add an assignment
        driver.findElement(By.cssSelector(".MuiButtonBase-root:nth-child(4)")).click();
        driver.findElement(By.cssSelector(".MuiDialog-container")).click();
        Thread.sleep(SLEEP_DURATION);
        driver.findElement(By.id(":rb:")).sendKeys("hw 1");
        driver.findElement(By.id(":rd:")).sendKeys("2024-02-16");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);
        Thread.sleep(SLEEP_DURATION);

        //Grade assignment
        driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(4) > .MuiButtonBase-root")).click();
        driver.findElement(By.name("score")).sendKeys("91");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        // Assertion to check if the section was added successfully
        String expectedNotification = "Grades saved";
        boolean isPresent = driver.getPageSource().contains(expectedNotification);
        assertTrue(isPresent, "The grade was saved.");
        Thread.sleep(SLEEP_DURATION);

        //close
        driver.findElement(By.id("close")).click();
        Thread.sleep(SLEEP_DURATION);
    }
}
