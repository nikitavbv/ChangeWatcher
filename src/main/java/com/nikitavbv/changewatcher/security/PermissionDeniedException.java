package com.nikitavbv.changewatcher.security;

/**
 * Thrown if user does not have access to the requested resource.
 *
 * @author Nikita Volobuev
 */
public class PermissionDeniedException extends RuntimeException {

  public PermissionDeniedException(String msg) {
    super(msg);
  }

}
