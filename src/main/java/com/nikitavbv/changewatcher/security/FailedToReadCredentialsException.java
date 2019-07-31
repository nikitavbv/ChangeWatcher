package com.nikitavbv.changewatcher.security;

import org.springframework.security.core.AuthenticationException;

/** Thrown if failed to read ApplicationUser credentials. **/
public class FailedToReadCredentialsException extends AuthenticationException {

  /** Constructs FailedToReadCredentialsException. */
  public FailedToReadCredentialsException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
