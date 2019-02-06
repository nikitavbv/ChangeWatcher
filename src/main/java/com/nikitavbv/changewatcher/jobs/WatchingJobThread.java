package com.nikitavbv.changewatcher.jobs;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WatchingJobThread extends Thread {

  private static final int DISPLAY_NUMBER = 98;
  private static final String XVFB_PATH = "/usr/bin/Xvfb";
  private static final String XVFB_COMMAND = XVFB_PATH + ":" + DISPLAY_NUMBER;

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
    environment.put("DISPLAY", ":" + DISPLAY_NUMBER);
    final GeckoDriverService service = new GeckoDriverService.Builder()
        .usingAnyFreePort()
        .withEnvironment(environment)
        .build();
    final WebDriver driver = new FirefoxDriver(service);
    driver.get(job.getUrl());

    final File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    FileUtils.copyFile(scrFile, targetFile);
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
