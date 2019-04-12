package com.nikitavbv.changewatcher.api;

/**
 * Returned by controllers if error occurred while processing request.
 *
 * @author Nikita Volobuev
 */
public class ErrorResponse {

  /** Error name. */
  private String error;
  /** Error message. */
  private String message;

  /** Construct ErrorResponse given error name and message. */
  public ErrorResponse(String error, String message) {
    this.error = error;
    this.message = message;
  }

  /** Construct ErrorResponse given error name. */
  public ErrorResponse(String error) {
    this.error = error;
  }

  /** Returns error name. */
  public String getError() {
    return error;
  }

  /** Returns error message. */
  public String getMessage() {
    return message;
  }

}
