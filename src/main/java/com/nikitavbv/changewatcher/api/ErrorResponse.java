package com.nikitavbv.changewatcher.api;

/**
 * Returned by controllers if error occurred while processing request.
 *
 * @author Nikita Volobuev
 */
public class ErrorResponse {

  private String error;
  private String message;

  public ErrorResponse(String error, String message) {
    this.error = error;
    this.message = message;
  }

  public ErrorResponse(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

}
