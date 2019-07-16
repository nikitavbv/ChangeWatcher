package com.nikitavbv.changewatcher.api;

/**
 * Response to indicate that request was processed successfully,
 * but no other data is provided.
 *
 * @author Nikita Volobuev
 */
public class StatusOkResponse {

  /** Status OK constant. */
  private static final String STATUS_OK = "OK";

  /** Returns status. */
  public String getStatus() {
    return STATUS_OK;
  }

}
