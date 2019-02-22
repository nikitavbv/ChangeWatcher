package com.nikitavbv.changewatcher;

import static com.nikitavbv.changewatcher.security.SecurityConstants.SECRET_SIZE;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.security")
public class SecurityProperties {
  String secret;

  public String generateSecret() {
    SecureRandom random = new SecureRandom();
    byte[] randomBytes = new byte[SECRET_SIZE];
    random.nextBytes(randomBytes);
    return Base64.getEncoder().encodeToString(randomBytes);
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

}
