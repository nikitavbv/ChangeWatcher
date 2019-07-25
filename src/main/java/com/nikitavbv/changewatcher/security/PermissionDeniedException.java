package com.nikitavbv.changewatcher.security;

/**
 * Thrown if user does not have access to the requested resource.
 *
 * @author Nikita Volobuev
 */
public class PermissionDeniedException extends RuntimeException {

  /**
   * Creates PermissionDeniedException.
   *
   * @param msg details
   * */
  public PermissionDeniedException(final String msg) {
    super(msg);
  }
}
