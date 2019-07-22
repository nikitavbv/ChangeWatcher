package com.nikitavbv.changewatcher.preview;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 * Thread to generate web page preview screenshots.
 *
 * @author Nikita Volobuev
 */
public class GeneratePreviewThread extends Thread {

  /** Logger for this class. */
  private static final Logger LOG = Logger.getLogger(GeneratePreviewThread.class.getName());

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

  /** Page url to make preview of. */
  private final String url;
  /** Preview ID, or name of the file to save preview to. */
  private final String previewID;
  /** Directory where page previews are stored. */
  private final String previewsDir;

  /**
   * Creates GeneratePreviewThread.
   *
   * @param url link to page to generate preview of
   * @param previewID name of the preview file
   * @param previewsDir directory where previews are saved to
   */
  GeneratePreviewThread(String url, String previewID, String previewsDir) {
    super();
    this.url = url;
    this.previewID = previewID;
    this.previewsDir = previewsDir;
  }

  /**
   * Run preview generation.
   */
  public void run() {
    try {
      final Process xvfbProcess = Runtime.getRuntime().exec(XVFB_COMMAND);
      final Map<String, String> environment = new HashMap<>();
      environment.put("DISPLAY", ":" + DISPLAY_NUMBER + ".0");
      System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_PATH);
      final GeckoDriverService service = new GeckoDriverService.Builder()
          .usingAnyFreePort()
          .withEnvironment(environment)
          .build();
      final WebDriver driver = new FirefoxDriver(service);
      driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
      driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
      try {
        driver.get(url);
      } catch (Exception e) {
        if (LOG.isLoggable(Level.WARNING)) {
          LOG.warning("Exception while getting page: " + e.getMessage());
        }
      }
      Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
          .takeScreenshot(driver);

      final File targetFile = new File(
              previewsDir + previewID + "." + PreviewController.PREVIEW_IMAGE_FORMAT
      );
      if (!targetFile.getParentFile().exists()) {
        boolean result = targetFile.getParentFile().mkdirs();
        if (!result) {
          LOG.warning("Failed to make dirs for preview directory");
        }
      }
      if (!targetFile.exists()) {
        boolean result = targetFile.createNewFile();
        if (!result) {
          LOG.warning("Failed to create new file for preview");
        }
      }

      ImageIO.write(
              screenshot.getImage(),
              PreviewController.PREVIEW_IMAGE_FORMAT.toUpperCase(Locale.getDefault()),
              targetFile
      );
      driver.close();
      xvfbProcess.destroy();
    } catch (Exception e) {
      if (LOG.isLoggable(Level.WARNING)) {
        LOG.warning("Failed to generate url preview: " + e.getMessage());
      }
    }
  }
}
