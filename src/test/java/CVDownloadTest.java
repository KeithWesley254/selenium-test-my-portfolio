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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // Step 1: Wait for wrapper to be in the DOM
            WebElement wrapper = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".btn-wrapper")));

            // Step 2: Scroll to the wrapper to trigger the reveal animation
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", wrapper);

            // Step 3: Wait for the class 'revealed' to be added
            wait.until(driver1 -> wrapper.getAttribute("class").contains("revealed"));

            // Step 4: Locate the button and validate it
            WebElement downloadButton = wrapper.findElement(By.cssSelector("a.btn.btn-primary"));

            Assertions.assertTrue(downloadButton.isDisplayed(), "✅ Download button is visible");
            Assertions.assertTrue(downloadButton.isEnabled(), "✅ Download button is clickable");

            String href = downloadButton.getAttribute("href");
            Assertions.assertTrue(href.endsWith("prof-cv.pdf"), "✅ Href points to the CV PDF");

            System.out.println("✅ CV download button found and validated.");

        } catch (TimeoutException e) {
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