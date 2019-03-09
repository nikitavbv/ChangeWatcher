package com.nikitavbv.changewatcher.preview;

/**
 * API response to generate preview request.
 * Contains screenshot id.
 *
 * @author Nikita Volobuev
 */
public class WebpagePreviewResponse {

  String screenshotID;

  WebpagePreviewResponse(String screenshotID) {
    this.screenshotID = screenshotID;
  }

  public String getScreenshotID() {
    return screenshotID;
  }

}
