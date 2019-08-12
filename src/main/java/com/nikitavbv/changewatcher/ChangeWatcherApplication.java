package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.config.ApplicationProperties;
import com.nikitavbv.changewatcher.config.SecurityProperties;
import com.nikitavbv.changewatcher.config.WatchingJobProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Main Application class.
 */
@SpringBootApplication
@EnableConfigurationProperties({
    SecurityProperties.class,
    ApplicationProperties.class,
    WatchingJobProperties.class
})
@EnableScheduling
@SuppressWarnings("PMD.UseUtilityClass")
public class ChangeWatcherApplication {

  /**
   * Start Spring application.
   */
  public static void main(final String[] args) {
    SpringApplication.run(ChangeWatcherApplication.class, args);
  }

  /** This bean provides BCrypt encoder for password hashing. */
  @Bean(name = "bCryptPasswordEncoder")
  public BCryptPasswordEncoder bcryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

}

