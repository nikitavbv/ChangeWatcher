package com.nikitavbv.changewatcher.preview;

public class WebpagePreviewResponse {

  String screenshotID;

  WebpagePreviewResponse(String screenshotID) {
    this.screenshotID = screenshotID;
  }

  public String getScreenshotID() {
    return screenshotID;
  }

}
