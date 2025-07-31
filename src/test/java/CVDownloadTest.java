import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
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
    public void verifyDownloadCVButtonExists() throws IOException, InterruptedException {
        driver.get("https://keithwesley254.github.io/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            WebElement wrapper = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-wrapper")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", wrapper);
            wait.until(d -> wrapper.getAttribute("class").contains("revealed"));
            Thread.sleep(1000); // allow for smooth animations

            WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".btn-wrapper.revealed a.btn.btn-primary")));

            Assertions.assertTrue(downloadButton.isDisplayed(), "✅ Download button is visible");
            Assertions.assertTrue(downloadButton.isEnabled(), "✅ Download button is clickable");

            String href = downloadButton.getAttribute("href");
            Assertions.assertTrue(href.endsWith("prof-cv.pdf"), "✅ Href points to the CV PDF");

            // ✅ Check file is reachable (status code 200)
            HttpURLConnection connection = (HttpURLConnection) new URL(href).openConnection();
            connection.setRequestMethod("HEAD");
            int statusCode = connection.getResponseCode();
            Assertions.assertEquals(200, statusCode, "✅ CV PDF is downloadable (HTTP 200)");

            // Optional: save success screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get("target/success-screenshot.png"), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ CV download button found, visible, enabled, and href validated.");
        } catch (TimeoutException | AssertionError e) {
            File failureScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(failureScreenshot.toPath(), Paths.get("target/failure-screenshot.png"), StandardCopyOption.REPLACE_EXISTING);
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