import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URL;

public class CVDownloadTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() throws Exception {
        String remoteUrl = System.getProperty("selenium.remote.url", "http://localhost:4444/wd/hub");
        ChromeOptions options = new ChromeOptions();
        driver = new RemoteWebDriver(new URL(remoteUrl), options);
    }

    @Test
    public void verifyDownloadCVButtonExists() {
        driver.get("https://keithwesley254.github.io/");

        // Use XPath to find the anchor tag by link text
        WebElement downloadButton = driver.findElement(By.xpath("//a[text()='Download My CV']"));

        // Check if it's displayed and enabled
        Assertions.assertTrue(downloadButton.isDisplayed(), "Download button is visible");
        Assertions.assertTrue(downloadButton.isEnabled(), "Download button is clickable");

        // Optional: assert the href contains the PDF
        String href = downloadButton.getAttribute("href");
        Assertions.assertTrue(href.endsWith("prof-cv.pdf"), "Href points to the CV PDF");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}