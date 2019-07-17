package com.nikitavbv.changewatcher.jobs;

/**
 * Thrown if requested screenshot is not found.
 * Requested screenshot has not been taken yet, is deleted or never existed.
 *
 * @author Nikita Volobuev
 */
public class ScreenshotNotFoundException extends RuntimeException {

  ScreenshotNotFoundException() {
    super("Screenshot not found");
  }

  ScreenshotNotFoundException(Throwable e) {
    super("Screenshot not found", e);
  }
}
