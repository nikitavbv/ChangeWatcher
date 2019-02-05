package com.nikitavbv.changewatcher.api;

public class ErrorResponse {

  private String error;
  private String message;

  public ErrorResponse(String error, String message) {
    this.error = error;
    this.message = message;
  }

}
