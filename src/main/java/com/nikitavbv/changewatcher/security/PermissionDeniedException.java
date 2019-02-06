package com.nikitavbv.changewatcher.security;

public class PermissionDeniedException extends RuntimeException {

  public PermissionDeniedException(String msg) {
    super(msg);
  }

}
