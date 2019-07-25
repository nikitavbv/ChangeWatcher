package com.nikitavbv.changewatcher.preview;

/**
 * API request to generate web page preview.
 *
 * @author Nikita Volobuev
 */
public class WebpagePreviewRequest {

  /** URL of the page to generate preview of. */
  private String url;

  /**
   * Get webpage url to generate preiew of.
   *
   * @return webpage url
   */
  /* default */ String getUrl() {
    return url;
  }

  /**
   * Set webpage preview to generate preview of.
   *
   * @param url webpage url
   */
  public void setUrl(final String url) {
    this.url = url;
  }
}
