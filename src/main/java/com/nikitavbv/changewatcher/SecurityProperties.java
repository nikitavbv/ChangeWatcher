package com.nikitavbv.changewatcher;

import static com.nikitavbv.changewatcher.security.SecurityConstants.SECRET_SIZE;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application security configuration. Set by app.security.
 *
 * @author Nikita Volobuev
 */
@ConfigurationProperties("app.security")
public class SecurityProperties {
  /** Secret token. */
  private String secret;

  /**
   * Generates secret token.
   *
   * @return generated token.
   */
  public String generateSecret() {
    final SecureRandom random = new SecureRandom();
    final byte[] randomBytes = new byte[SECRET_SIZE];
    random.nextBytes(randomBytes);
    return Base64.getEncoder().encodeToString(randomBytes);
  }

  /** Returns secret token. */
  public String getSecret() {
    return secret;
  }

  /** Set secret token. */
  public void setSecret(final String secret) {
    this.secret = secret;
  }

}
