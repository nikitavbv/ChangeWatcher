package com.nikitavbv.changewatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;

/**
 * Abstract thread for all jobs working with webdriver.
 */
public abstract class AbstractWebdriverThread extends Thread {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(AbstractWebdriverThread.class.getName());

    /** Browser window width when taking screenshot. */
    private static final int WINDOW_WIDTH = 1366;
    /** Browser window height when taking screenshot. */
    private static final int WINDOW_HEIGHT = 768;
    /** Image depth of virtual display where browser is launched. */
    private static final int IMAGE_DEPTH = 24;
    /** Screen mode of virtual display where browser is launched. */
    private static final String SCREEN_MODE = WINDOW_WIDTH + "x" + WINDOW_HEIGHT + "x" + IMAGE_DEPTH;

    /** Number of virtual display used for browser. */
    private static final int DISPLAY_NUMBER = 1001;
    /** Path to xvfb which is used to create virtual display. */
    private static final String XVFB_PATH = "/usr/bin/Xvfb";
    /** xvfb command to create virtual display. */
    private static final String XVFB_COMMAND = XVFB_PATH
            + " -br -nolisten tcp -screen 0 " + SCREEN_MODE + " :" + DISPLAY_NUMBER;
    /** Path to geckodriver which is used to control firefox. */
    private static final String GECKO_DRIVER_PATH = "/geckodriver/geckodriver";

    /** Max number of seconds we wait before page is considered loaded. */
    private static final int PAGE_LOAD_TIMEOUT = 10;

    private Process xvfbProcess;
    private WebDriver driver;

    /** Creates and sets up WebDriver. */
    protected WebDriver getWebDriver() {
        if (driver != null) {
            return driver;
        }

        if (startXvfbProcess() == null) {
            return null;
        }

        final Map<String, String> environment = new HashMap<>();
        environment.put("DISPLAY", ":" + DISPLAY_NUMBER + ".0");
        System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_PATH);

        final GeckoDriverService service = new GeckoDriverService.Builder()
                .usingAnyFreePort()
                .withEnvironment(environment)
                .build();
        driver = new FirefoxDriver(service);
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        return driver;
    }

    /** Starts virtual frame buffer process. */
    private Process startXvfbProcess() {
        try {
            xvfbProcess = Runtime.getRuntime().exec(XVFB_COMMAND);
        } catch (IOException e) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "Failed to run xvfb process", e);
            }
            return null;
        }
        return xvfbProcess;
    }

    /** Stops virtual frame buffer, closes web driver. */
    protected void disposeDriver() {
        driver.close();
        xvfbProcess.destroy();
    }
}
