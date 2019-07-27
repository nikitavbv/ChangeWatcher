package com.nikitavbv.changewatcher.security;

import org.springframework.security.core.AuthenticationException;

/** Thrown if failed to read ApplicationUser credentials. **/
public class FailedToReadCredentialsException extends AuthenticationException {

    /** Constructs FailedToReadCredentialsException. */
    public FailedToReadCredentialsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
