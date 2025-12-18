package com.example.tama_gargoyles.controllers;

import com.example.tama_gargoyles.repository.GargoyleRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedNPlayFeatureTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    GargoyleRepository gargoyleRepository;

    private final String AUTH0_USERNAME = "akshat@gmail.com";
    private final String AUTH0_PASSWORD = "Test123$";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    private void loginToAuth0() {
        driver.get("http://localhost:8080/game"); // Trigger redirect to Auth0 login

        // Wait for Auth0 login page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        // Fill in Auth0 credentials
        driver.findElement(By.name("username")).sendKeys(AUTH0_USERNAME);
        driver.findElement(By.name("password")).sendKeys(AUTH0_PASSWORD);
        driver.findElement(By.name("action")).click(); // Login button

        // Wait until redirected back to the game page
        wait.until(d -> d.getCurrentUrl().endsWith("/"));
    }

    @Test
    void feedingGargoyleIncreasesHunger() {
        loginToAuth0();

        WebElement hungerSpan = driver.findElement(By.xpath("//strong[text()='Hunger:']/following-sibling::span"));
        int hungerBefore = Integer.parseInt(hungerSpan.getText());

        driver.findElement(By.xpath("//button[text()='Feed']")).click();

        wait.until(d -> d.getCurrentUrl().endsWith("/game"));

        WebElement hungerAfterSpan = driver.findElement(By.xpath("//strong[text()='Hunger:']/following-sibling::span"));
        int hungerAfter = Integer.parseInt(hungerAfterSpan.getText());

        assertThat(hungerAfter)
                .isGreaterThanOrEqualTo(hungerBefore)
                .isLessThanOrEqualTo(100);
    }

    @Test
    void playingGargoyleIncreasesHappiness() {
        loginToAuth0();

        WebElement happinessSpan = driver.findElement(By.xpath("//strong[text()='Happiness:']/following-sibling::span"));
        int happinessBefore = Integer.parseInt(happinessSpan.getText());

        driver.findElement(By.xpath("//button[text()='Play']")).click();

        wait.until(d -> d.getCurrentUrl().endsWith("/game"));

        WebElement happinessAfterSpan = driver.findElement(By.xpath("//strong[text()='Happiness:']/following-sibling::span"));
        int happinessAfter = Integer.parseInt(happinessAfterSpan.getText());

        assertThat(happinessAfter)
                .isGreaterThanOrEqualTo(happinessBefore)
                .isLessThanOrEqualTo(100);
    }
}
