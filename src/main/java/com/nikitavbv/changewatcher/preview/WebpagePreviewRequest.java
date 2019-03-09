package com.nikitavbv.changewatcher.preview;

/**
 * API request to generate web page preview.
 *
 * @author Nikita Volobuev
 */
public class WebpagePreviewRequest {

  private String url;

  String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
