package com.nikitavbv.changewatcher.preview;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class GeneratePreviewThread extends Thread {

  private static final int WINDOW_WIDTH = 1366;
  private static final int WINDOW_HEIGHT = 768;
  private static final int IMAGE_DEPTH = 24;
  private static final String SCREEN_MODE = WINDOW_WIDTH + "x" + WINDOW_HEIGHT + "x" + IMAGE_DEPTH;

  private static final int DISPLAY_NUMBER = 1001;
  private static final String XVFB_PATH = "/usr/bin/Xvfb";
  private static final String XVFB_COMMAND = XVFB_PATH +
      " -br -nolisten tcp -screen 0 " + SCREEN_MODE + " :" + DISPLAY_NUMBER;
  private static final String GECKO_DRIVER_PATH = "/geckodriver/geckodriver";

  private static final int PAGE_LOAD_TIMEOUT = 10;

  private String url;
  private String previewID;
  private String previewsDir;

  GeneratePreviewThread(String url, String previewID, String previewsDir) {
    super();
    this.url = url;
    this.previewID = previewID;
    this.previewsDir = previewsDir;
  }

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
      } catch(Exception e) {
        // ignore;
      }
      Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
          .takeScreenshot(driver);

      final File targetFile = new File(
              previewsDir + previewID + "." + PreviewController.PREVIEW_IMAGE_FORMAT
      );
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

      ImageIO.write(
              screenshot.getImage(),
              PreviewController.PREVIEW_IMAGE_FORMAT.toUpperCase(),
              targetFile
      );
      driver.close();
      xvfbProcess.destroy();
    } catch(Exception e) {
      System.err.println("Failed to generate url preview");
      e.printStackTrace();
    }
  }

}
