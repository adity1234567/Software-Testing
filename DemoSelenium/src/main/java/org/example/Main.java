package org.example;
/*
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        WebDriver driver = WebDriverManager.getDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Implicit wait

        try {
            driver.get("https://developer.mozilla.org/en-US/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Extended wait time

            // Locate and click the theme toggle button
            WebElement themeToggleButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".theme-switcher-menu button")));
            themeToggleButton.click();

            // Automatically toggle between Dark and Light theme
            for (int i = 0; i < 2; i++) { // Toggles twice for demonstration
                toggleTheme(wait, driver, "dark");
                toggleTheme(wait, driver, "light");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WebDriverManager.quitDriver();
        }
    }

    private static void toggleTheme(WebDriverWait wait, WebDriver driver, String theme) throws IOException, InterruptedException {
        try {
            // Select theme based on the specified option
            WebElement themeOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("ul.themes-menu button .icon-theme-" + theme)));
            themeOption.click();
        } catch (Exception e) {
            // Fallback using JavaScript if the click fails
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement themeOption = driver.findElement(By.cssSelector("ul.themes-menu button .icon-theme-" + theme));
            js.executeScript("arguments[0].click();", themeOption);
        }

        System.out.println("Switched to " + theme + " mode.");

        // Wait briefly to let the theme apply
        Thread.sleep(3000);

        // Take a screenshot for verification
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String fileName = theme + "_mode_screenshot_" + LocalDateTime.now().toString().replace(":", "-") + ".png";
        FileUtils.copyFile(screenshot, new File(fileName));
        System.out.println("Screenshot taken for " + theme + " mode: " + fileName);

        // Log the time of the theme change
        logThemeChange(theme);
    }

    private static void logThemeChange(String theme) throws IOException {
        // Log theme change with timestamp
        String logFileName = "theme_change_log.txt";
        String logEntry = "Switched to " + theme + " mode at " + LocalDateTime.now() + "\n";
        try (FileWriter writer = new FileWriter(logFileName, true)) {
            writer.write(logEntry);
        }
        System.out.println("Logged theme change: " + logEntry);
    }
}
*/

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
//import utilities.WebDriverManager;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class Main {

    private static final String URL = "https://developer.mozilla.org/en-US/docs/Web/WebDriver";

    public static void main(String[] args) {
        Main test = new Main();
        test.runAllTests();
    }

    private void runAllTests() {
        List<String> themes = Arrays.asList("light", "dark", "os-default");
        List<List<String>> themeSequences = new ArrayList<>();
        generatePermutations(themes, 0, themeSequences);

        for (List<String> sequence : themeSequences) {
            runThemeTest(sequence);
        }
    }
    private void generatePermutations(List<String> themes, int start, List<List<String>> results) {
        if (start == themes.size() - 1) {
            results.add(new ArrayList<>(themes));
            return;
        }

        for (int i = start; i < themes.size(); i++) {
            Collections.swap(themes, start, i);
            generatePermutations(themes, start + 1, results);
            Collections.swap(themes, start, i); // backtrack
        }
    }

    private void runThemeTest(List<String> themes) {
        WebDriver driver = WebDriverManager.getDriver();
        String expectedTheme = themes.get(themes.size() - 1);


        driver.get(URL);


        for (String theme : themes) {
            switchToTheme(driver, theme);
            showNotification(driver, theme);
            verifyTheme(driver, theme); // Verify the current theme
        }

        // Verify the final expected theme
        verifyTheme(driver, expectedTheme);
        WebDriverManager.quitDriver(); // Quit the driver after the test
    }


    private void switchToTheme(WebDriver driver, String theme) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement themeSwitcher = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".theme-switcher-menu")));
        themeSwitcher.click();

        String themeSelector;
        switch (theme) {
            case "light":
                themeSelector = ".icon-theme-light";
                break;
            case "dark":
                themeSelector = ".icon-theme-dark";
                break;
            case "os-default":
                themeSelector = ".icon-theme-os-default";
                break;
            default:
                throw new IllegalArgumentException("Invalid theme: " + theme);
        }

        WebElement themeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(themeSelector)));
        themeButton.click();


        System.out.println("Time: " + LocalDateTime.now() + " - Changed theme to: " + theme);


        try {
            Thread.sleep(4000); // Increased delay to make each change more noticeable
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void showNotification(WebDriver driver, String theme) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // JavaScript to create a notification banner
        String script = "let notification = document.createElement('div');" +
                "notification.innerText = 'Theme changed to: " + theme + "';" +
                "notification.style.position = 'fixed';" +
                "notification.style.top = '0';" +
                "notification.style.width = '100%';" +
                "notification.style.backgroundColor = 'green';" +
                "notification.style.color = 'white';" +
                "notification.style.padding = '10px';" +
                "notification.style.textAlign = 'center';" +
                "notification.style.zIndex = '10000';" +
                "document.body.appendChild(notification);" +
                "setTimeout(() => notification.remove(), 3000);"; // Remove after 3 seconds

        // Execute the JavaScript in the browser
        js.executeScript(script);

        // Console log for debugging
        System.out.println("Notification shown for theme: " + theme);
    }

    // Verify that the current theme matches the expected theme
    private void verifyTheme(WebDriver driver, String expectedTheme) {
        String currentClass = driver.findElement(By.tagName("html")).getAttribute("class");
        assert currentClass.contains(expectedTheme) : expectedTheme + " theme was not applied. Current class: " + currentClass;
        System.out.println("Time: " + LocalDateTime.now() + " - " + expectedTheme + " theme applied successfully.");
    }
}