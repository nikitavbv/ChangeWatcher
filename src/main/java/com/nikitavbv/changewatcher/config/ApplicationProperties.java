package com.nikitavbv.changewatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Contains general application properties defined by app prefix.
 *
 * @author Nikita Volobuev
 */
@ConfigurationProperties("app.general")
public class ApplicationProperties {

  /** Directory where application data (like screenshots) is stored. */
  private final String dataDir;

  /** Creates application properties. */
  public ApplicationProperties(String dataDir) {
    this.dataDir = dataDir;
  }

  /** Returns path to the directory where application data is stored. */
  public String getDataDir() {
    return dataDir;
  }
}
