package com.example.tama_gargoyles.controllers;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RenameFeatureTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String AUTH0_USERNAME = "akshat@gmail.com";
    private final String AUTH0_PASSWORD = "Test123$";

    @Autowired
    GargoyleRepository gargoyleRepository;

    @Autowired
    UserRepository userRepository;

    private Gargoyle gargoyle;

    @BeforeAll
    void setupDriver() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeEach
    void setupData() {
        User user = new User("test@example.com");
        user = userRepository.save(user);

        gargoyle = new Gargoyle();
        gargoyle.setName("OldName");
        gargoyle.setHunger(50);
        gargoyle.setHappiness(50);
        gargoyle.setUser(user);

        gargoyle = gargoyleRepository.save(gargoyle);
    }

    @AfterEach
    void cleanup() {
        gargoyleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    void teardown() {
        driver.quit();
    }

    private void loginToAuth0() {
        driver.get("http://localhost:8080/game");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        driver.findElement(By.name("username")).sendKeys(AUTH0_USERNAME);
        driver.findElement(By.name("password")).sendKeys(AUTH0_PASSWORD);
        driver.findElement(By.name("action")).click();

        wait.until(ExpectedConditions.urlContains("/game"));
    }

    @Test
    void renamingGargoyleUpdatesName() {
        // LOGIN FIRST
        loginToAuth0();

        // Go to rename page
        driver.get("http://localhost:8080/gargoyle/" + gargoyle.getId() + "/rename");

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("name"))
        );

        nameInput.clear();
        nameInput.sendKeys("Sir Screams-a-Lot");

        // Click submit button explicitly
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // Wait for redirect
        wait.until(ExpectedConditions.urlContains("/game"));

        // Verify DB update
        Gargoyle updated = gargoyleRepository
                .findById(gargoyle.getId())
                .orElseThrow();

        assertThat(updated.getName()).isEqualTo("Sir Screams-a-Lot");
    }
}
