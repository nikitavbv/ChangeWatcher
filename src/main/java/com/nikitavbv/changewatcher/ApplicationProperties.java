package com.nikitavbv.changewatcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.general")
public class ApplicationProperties {

  private String dataDir;

  public String getDataDir() {
    return dataDir;
  }

}
