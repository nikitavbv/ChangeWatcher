package com.nikitavbv.changewatcher.api;

/**
 * Response to indicate that request was processed successfully, but no other data is provided.
 *
 * @author Nikita Volobuev
 */
public class StatusOkResponse {

  private String status = "OK";

  public String getStatus() {
    return status;
  }

}
