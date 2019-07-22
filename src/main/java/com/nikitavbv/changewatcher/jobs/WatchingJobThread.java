package com.nikitavbv.changewatcher.jobs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 * Thread to make web page screenshots and detect changes.
 *
 * @author Nikita Volobuev
 */
public class WatchingJobThread extends Thread {

  /** Logger for this class. */
  private final static Logger LOG = Logger.getLogger(WatchingJobThread.class.getName());

  /** Browser window width when taking screenshot. */
  private static final int WINDOW_WIDTH = 1366;
  /** Browser window height when taking screenshot. */
  private static final int WINDOW_HEIGHT = 768;
  /** Image depth of virtual display where browser is launched. */
  private static final int IMAGE_DEPTH = 24;
  /** Screen mode of virtual display where browser is launched. */
  private static final String SCREEN_MODE = WINDOW_WIDTH + "x" + WINDOW_HEIGHT + "x" + IMAGE_DEPTH;

  /** Number of virtual display used for browser. */
  private static final int DISPLAY_NUMBER = 1002;
  /** Path to xvfb which is used to create virtual display. */
  private static final String XVFB_PATH = "/usr/bin/Xvfb";
  /** xvfb command to create virtual display. */
  private static final String XVFB_COMMAND = XVFB_PATH
          + " -br -nolisten tcp -screen 0 " + SCREEN_MODE + " :" + DISPLAY_NUMBER;
  /** Path to geckodriver which is used to control firefox. */
  private static final String GECKO_DRIVER_PATH = "/geckodriver/geckodriver";

  /** Max number of seconds we wait before page is considered loaded. */
  private static final int PAGE_LOAD_TIMEOUT = 10;

  /** WatchingJobRepository to get job data and save updates. */
  private final WatchingJobRepository repository;
  /** Watching job specific to this thread. */
  private final WatchingJob job;
  /** Directory where screenshots are saved to. */
  private final String screenshotsDir;

  /**
   * Creates WatchingJobThread.
   *
   * @param repository WatchingJobRepository to get/update job info.
   * @param job watching job which will be checked in this thread.
   * @param screenshotsDir directory to save screenshot to.
   */
  WatchingJobThread(WatchingJobRepository repository, WatchingJob job, String screenshotsDir) {
    this.job = job;
    this.screenshotsDir = screenshotsDir;
    this.repository = repository;
  }

  /**
   * Run watching thread.
   */
  public void run() {
    try {
      File imageFile = websiteScreenshot();
      File prevImageFile = job.getPrevWebsiteScreenshotFile(screenshotsDir);
      long differentPixels = compareImages(imageFile, prevImageFile);
      job.setLastRunDifferentPixels(differentPixels);

      if (differentPixels >= job.getPixelDifferenceToTrigger()) {
        System.out.println("Job triggered: " + job.getID());
        job.runWebhook();
      } else {
        System.out.println("Job is not triggered: " + job.getID());
      }

      job.setLastCheckTime(System.currentTimeMillis());

      repository.save(job);
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Watching job failed", e);
    }
  }

  /** Make screenshot of page. */
  private File websiteScreenshot() throws IOException {
    final File targetFile = job.getWebsiteScreenshotFile(screenshotsDir);
    final File prevFile = job.getPrevWebsiteScreenshotFile(screenshotsDir);
    if (targetFile.exists()) {
      if (!prevFile.getParentFile().exists()) {
        boolean result = prevFile.getParentFile().mkdirs();
        if (!result) {
          LOG.warning("Failed to create parent dir for prev screenshot file");
        }
      }
      if (!prevFile.exists()) {
        boolean result = prevFile.createNewFile();
        if (!result) {
          LOG.warning("Failed to create file for prev screenshot");
        }
      }
      FileUtils.copyFile(targetFile, prevFile);
    }

    saveScreenshotToFile(targetFile);

    return targetFile;
  }

  /**
   * Save page screenshot to file.
   *
   * @param targetFile file to save screenshot to.
   */
  private void saveScreenshotToFile(File targetFile) throws IOException {
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
      driver.get(job.getUrl());
    } catch (Exception e) {
      LOG.warning("Exception while getting url: " + e.getMessage());
    }
    Screenshot screenshot = new AShot()
            .shootingStrategy(ShootingStrategies.viewportPasting(1000))
            .takeScreenshot(driver);

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
            WatchingJob.SCREENSHOT_IMAGE_FORMAT.toUpperCase(Locale.getDefault()),
            targetFile
    );
    driver.close();
    xvfbProcess.destroy();
  }

  /**
   * Get number of pixels different between two images.
   * Pixels are considered different if rgb's do not match.
   *
   * @param first file with first image.
   * @param second file with second image.
   * @return total different pixels
   */
  private long compareImages(File first, File second) throws IOException {
    if (!first.exists() || !second.exists()) {
      return -1;
    }
    return compareImages(ImageIO.read(first), ImageIO.read(second));
  }

  /**
   * Get number of pixels different between two images.
   * Pixels are considered different if rgb's do not match.
   *
   * @param first first image
   * @param second second image
   * @return total different pixels
   */
  private long compareImages(BufferedImage first, BufferedImage second) {
    long totalDifferentPixels = 0;
    int startX = Math.min(job.getSelectionX(), Math.min(first.getWidth(), second.getWidth()));
    int startY = Math.min(job.getSelectionY(), Math.min(first.getHeight(), second.getHeight()));
    int width = Math.min(
            Math.min(first.getWidth(), second.getWidth()),
            job.getSelectionX() + job.getSelectionWidth()
    );
    int height = Math.min(
        Math.min(first.getHeight(), second.getHeight()),
        job.getSelectionY() + job.getSelectionHeight()
    );

    for (int x = startX; x < width; x++) {
      for (int y = startY; y < height; y++) {
        int firstRgb = first.getRGB(x, y);
        int secondRgb = second.getRGB(x, y);
        if (firstRgb != secondRgb) {
          totalDifferentPixels++;
        }
      }
    }
    return totalDifferentPixels;
  }
}
