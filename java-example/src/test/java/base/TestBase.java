package base;


import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QA Lady on 3/16/2017.
 */
public class TestBase {

    public static ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    public WebDriver driver;
    public static WebDriverWait wait;
    public static WebDriverWait shortWait;
    public static WebDriverWait longWait;
    DesiredCapabilities capabilities = new DesiredCapabilities();

    private static List<WebDriver> drivers = new ArrayList<>();

    @Parameters({"browser"})
    @BeforeTest(alwaysRun = true)
    public void configDriver(String browser) {
        if (threadLocalDriver.get() != null) {
            driver = threadLocalDriver.get();
            return;
        }
        System.out.println("Starting beforeTest for " + browser + " on thread:  " + Thread.currentThread().hashCode());
        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
//            where is driver (when not added to system path but is available locally)
            System.setProperty("webdriver.chrome.driver", "C:/Dev_Tools/Drivers/chromedriver.exe");
            options.addArguments("start-maximized");
            //when is built with the project from resources folder
//          System.setProperty("webdriver.chrome.driver", TestBase.class.getResource("/drivers/chromedriver.exe").getFile());
            driver = new ChromeDriver(options);
            System.out.println(((HasCapabilities) driver).getCapabilities());
        } else if (browser.equalsIgnoreCase("firefox")) {
//        where is driver (when not added to system path but is available locally)
            System.setProperty("webdriver.gecko.driver", "C:/Dev_Tools/Drivers/geckodriver.exe");
//        when is built with the project from resources folder
//            System.setProperty("webdriver.gecko.driver", TestBase.class.getResource("/drivers/geckodriver.exe").getFile());
//            where is browser
            System.out.println("Starting ff from C:/Program Files/Mozilla Firefox/firefox.exe");
            capabilities.setCapability(FirefoxDriver.BINARY, "C:/Program Files/Mozilla Firefox/firefox.exe");

            //we may skip next capability when starting geckodriver for new FF just use new FirefoxDriver();
//        capabilities.setCapability(FirefoxDriver.MARIONETTE, true);

            capabilities.setCapability("unexpectedAlertBehaviour", "dismiss");
            driver = new FirefoxDriver(capabilities);
            System.out.println(((HasCapabilities) driver).getCapabilities());
        } else if (browser.equalsIgnoreCase("firefox_old")) {
//        where is driver
            System.setProperty("webdriver.gecko.driver", "C:/Dev_Tools/Drivers/geckodriver.exe");
            // where is browser
//            capabilities.setCapability(FirefoxDriver.BINARY, "C:/Dev_Tools/Drivers/ESR/firefox.exe");
            //deprecated way to say where is browser
//            driver = new FirefoxDriver(new FirefoxBinary(new File("C:\\Dev_Tools\\Drivers\\ESR\\firefox.exe")), new FirefoxProfile(), capabilities);

            // the way to start old Firefox til version 47 inclusive (Selenium 3.3.1 has issue with this approach)
//          capabilities.setCapability(FirefoxDriver.MARIONETTE, false);

//            workaround 2 for Selenium bug with old FireFox launch
            capabilities.setCapability("raisesAccessibilityExceptions", false);
            capabilities.setCapability("acceptSslCerts", true);
//            driver = new FirefoxDriver(new FirefoxOptions().setLegacy(true).addDesiredCapabilities(capabilities));
            //or set binary location during driver initialization
            driver = new FirefoxDriver(
                    new FirefoxOptions()
                            .setLegacy(true)
                            .setBinary(new FirefoxBinary(new File("C:/Dev_Tools/Drivers/ESR/firefox.exe"))).addDesiredCapabilities(capabilities));
            System.out.println(((HasCapabilities) driver).getCapabilities());
        } else if (browser.equalsIgnoreCase("ff_n")) {
//        where is driver
            System.setProperty("webdriver.gecko.driver", TestBase.class.getResource("/drivers/geckodriver.exe").getFile());
//            deprecated approach
//            driver = new FirefoxDriver(new FirefoxBinary(new File("C:\\Dev_Tools\\Drivers\\Nightly\\firefox.exe")));
//            new approach to let selenium know where is browser
            capabilities.setCapability(FirefoxDriver.BINARY, "C:/Dev_Tools/Drivers/Nightly/firefox.exe");
            driver = new FirefoxDriver(capabilities);
        } else if (browser.contains("ie")) {
            System.setProperty("webdriver.ie.driver", TestBase.class.getResource("/drivers/IEDriverServer.exe").getFile());
            capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
//        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            driver = new InternetExplorerDriver(capabilities);
            System.out.println(((HasCapabilities) driver).getCapabilities());
            //start IE with detailed log
//            InternetExplorerDriverService service = new InternetExplorerDriverService.Builder()
//                    .withLogLevel(InternetExplorerDriverLogLevel.TRACE)
//                    .withLogFile(new File("iedriver.log"))
//                    .build();
//            InternetExplorerDriver driver = new InternetExplorerDriver(service);
        }
        //set thread local driver
        threadLocalDriver.set(driver);
        drivers.add(driver);
        //assigning wait value to be use in explicit waits
        wait = new WebDriverWait(driver, 10);
        shortWait = new WebDriverWait(driver, 2);
        longWait = new WebDriverWait(driver, 20);
        if (!browser.equalsIgnoreCase("chrome")) {
            driver.manage().window().maximize();
        }
        driver.get("http://localhost/litecart/admin/");
        //implicit wait
//        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    }

    @AfterSuite
    public void quitDriver() {
        for (WebDriver webDriver : drivers) {
            webDriver.close();
            webDriver.quit();
        }
    }
}
