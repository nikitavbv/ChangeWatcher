package com.nikitavbv.changewatcher.preview;

/**
 * API response to generate preview request.
 * Contains screenshot id.
 *
 * @author Nikita Volobuev
 */
public class WebpagePreviewResponse {

  /** ID of screenshot generated. */
  /* default */ String screenshotID;

  /**
   * Creates webpage preview response.
   * @param screenshotID id of page screenshot.
   */
  WebpagePreviewResponse(final String screenshotID) {
    this.screenshotID = screenshotID;
  }

  /** Returns id of page screenshot. */
  public String getScreenshotID() {
    return screenshotID;
  }
}
