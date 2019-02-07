package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.preview.PreviewController;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WatchingJobThread extends Thread {

  private static final int WINDOW_WIDTH = 1366;
  private static final int WINDOW_HEIGHT = 768;
  private static final int IMAGE_DEPTH = 24;
  private static final String SCREEN_MODE = WINDOW_WIDTH + "x" + WINDOW_HEIGHT + "x" + IMAGE_DEPTH;

  private static final int DISPLAY_NUMBER = 98;
  private static final String XVFB_PATH = "/usr/bin/Xvfb";
  private static final String XVFB_COMMAND = XVFB_PATH +
      " -br -nolisten tcp -screen 0 " + SCREEN_MODE + " :" + DISPLAY_NUMBER;
  private static final String GECKO_DRIVER_PATH = "/geckodriver/geckodriver";

  private static final int PAGE_LOAD_TIMEOUT = 10;

  private WatchingJob job;
  private String screenshotsDir;

  WatchingJobThread(WatchingJob job, String screenshotsDir) {
    this.job = job;
    this.screenshotsDir = screenshotsDir;
  }

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
    } catch(IOException e) {
      System.err.println("Watching job failed");
      e.printStackTrace();
    }
  }

  private File websiteScreenshot() throws IOException {
    final File targetFile = job.getWebsiteScreenshotFile(screenshotsDir);
    final File prevFile = job.getPrevWebsiteScreenshotFile(screenshotsDir);
    FileUtils.copyFile(targetFile, prevFile);

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
    } catch(Exception e) {
      // ignore;
    }
    Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
        .takeScreenshot(driver);

    if (!targetFile.getParentFile().exists()) {
      boolean result = targetFile.getParentFile().mkdirs();
      if (!result) {
        System.err.println("Failed to make dirs for preview directory");
      }
    }
    if (!targetFile.exists()) {
      boolean result = targetFile.createNewFile();
      if (!result) {
        System.err.println("Failed to create new file for preview");
      }
    }

    ImageIO.write(screenshot.getImage(), WatchingJob.SCREENSHOT_IMAGE_FORMAT.toUpperCase(), targetFile);
    driver.close();
    xvfbProcess.destroy();

    return targetFile;
  }

  private long compareImages(File first, File second) throws IOException {
    return compareImages(ImageIO.read(first), ImageIO.read(second));
  }

  private long compareImages(BufferedImage first, BufferedImage second) {
    long totalDifferentPixels = 0;
    for (int x = 0; x < Math.min(first.getWidth(), second.getWidth()); x++) {
      for (int y = 0; y < Math.min(first.getHeight(), second.getHeight()); y++) {
        int firstRGB = first.getRGB(x, y);
        int secondRGB = second.getRGB(x, y);
        if (firstRGB != secondRGB) {
          totalDifferentPixels++;
        }
      }
    }
    return totalDifferentPixels;
  }

}
