package com.nikitavbv.changewatcher.jobs;

public class ScreenshotNotFoundException extends RuntimeException {

  ScreenshotNotFoundException() {
    super("Screenshot not found");
  }

}
