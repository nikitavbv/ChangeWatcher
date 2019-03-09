package com.nikitavbv.changewatcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Contains general application properties defined by app.general.
 *
 * @author Nikita Volobuev
 */
@Configuration
@ConfigurationProperties("app.general")
public class ApplicationProperties {

  private String dataDir;

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }

}
