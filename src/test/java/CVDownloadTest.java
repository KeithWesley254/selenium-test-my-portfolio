import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.io.File;
import java.io.IOException;

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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(text(), 'Download My CV')]")));

            Assertions.assertTrue(downloadButton.isDisplayed(), "Download button is visible");
            Assertions.assertTrue(downloadButton.isEnabled(), "Download button is clickable");

            String href = downloadButton.getAttribute("href");
            Assertions.assertTrue(href.endsWith("prof-cv.pdf"), "Href points to the CV PDF");

            // ✅ Log success message
            System.out.println("✅ CV download button found and validated.");

        } catch (TimeoutException e) {
            // ❌ Print URL and take screenshot on failure
            System.out.println("❌ Timeout while waiting for CV button.");
            System.out.println("Failed URL: " + driver.getCurrentUrl());

            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            File dest = new File("target/failure-screenshot.png");
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
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