package com.nikitavbv.changewatcher.security;

/**
 * Security constants like token configuration.
 *
 * @author Nikita Volobuev
 */
public class SecurityConstants {
  /** Auth token prefix as stored in HTTP auth header. */
  public static final String TOKEN_PREFIX = "Bearer ";
  /** HTTP header for auth info. */
  public static final String HEADER_STRING = "Authorization";
  /** Auth token TTL. */
  public static final long EXPIRATION_TIME = 864_000_000; // 10 days
  /** Length of application secret. */
  public static final int SECRET_SIZE = 512 / 8; // in bytes
}
