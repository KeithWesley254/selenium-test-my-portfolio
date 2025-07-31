import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CVDownloadTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() throws Exception {
        String remoteUrl = System.getProperty("selenium.remote.url", "http://localhost:4444/wd/hub");
        ChromeOptions options = new ChromeOptions();
        driver = new RemoteWebDriver(new URL(remoteUrl), options);
    }

    @Test
    public void verifyDownloadCVButtonExists() throws IOException {
        driver.get("https://keithwesley254.github.io/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // Wait until wrapper is present
            WebElement wrapper = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".btn-wrapper")));

            // Scroll wrapper into view to trigger animation
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", wrapper);

            // Wait for 'revealed' class on wrapper using a custom ExpectedCondition
            wait.until((ExpectedCondition<Boolean>) drv ->
                    wrapper.getAttribute("class").contains("revealed"));

            // Now wait until the download button is both present and visible inside the wrapper
            WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".btn-wrapper.revealed a.btn.btn-primary")));

            // Extra safety: wait until it's clickable
            wait.until(ExpectedConditions.elementToBeClickable(downloadButton));

            // Final assertions
            Assertions.assertTrue(downloadButton.isDisplayed(), "✅ Download button is visible");
            Assertions.assertTrue(downloadButton.isEnabled(), "✅ Download button is enabled");

            String href = downloadButton.getAttribute("href");
            Assertions.assertNotNull(href, "❌ Button href is null");
            Assertions.assertTrue(href.endsWith("prof-cv.pdf"), "✅ Href points to the CV PDF");

            System.out.println("✅ CV download button found, visible, enabled, and href validated.");

        } catch (TimeoutException | NoSuchElementException e) {
            System.err.println("❌ CV download button test failed: " + e.getMessage());

            // Save screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get("target/failure-screenshot.png"),
                    StandardCopyOption.REPLACE_EXISTING);

            // Save page source
            Files.write(Paths.get("target/failure-page-source.html"), driver.getPageSource().getBytes());

            throw e;
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}