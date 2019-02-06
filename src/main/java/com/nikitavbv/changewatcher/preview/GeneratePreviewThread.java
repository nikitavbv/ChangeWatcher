package com.nikitavbv.changewatcher.preview;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeneratePreviewThread extends Thread {

  private static final int DISPLAY_NUMBER = 99;
  private static final String XVFB_PATH = "/usr/bin/Xvfb";
  private static final String XVFB_COMMAND = XVFB_PATH + " :" + DISPLAY_NUMBER;

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
      environment.put("DISPLAY", ":" + DISPLAY_NUMBER);
      final GeckoDriverService service = new GeckoDriverService.Builder()
          .usingAnyFreePort()
          .withEnvironment(environment)
          .build();
      final WebDriver driver = new FirefoxDriver(service);
      driver.get(url);
      final File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      final File targetFile =  new File(previewsDir + "/" + previewID + "." + PreviewController.PREVIEW_IMAGE_FORMAT);
      FileUtils.copyFile(scrFile, targetFile);
      driver.close();
      xvfbProcess.destroy();
    } catch(IOException e) {
      System.out.println("Failed to generate url preview");
      e.printStackTrace();
    }
  }

}
