package com.nikitavbv.changewatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application class
 */
@SpringBootApplication
public class ChangeWatcherApplication {

  private ChangeWatcherApplication() {}

  /**
   * Start Spring application
   */
  public static void main(final String[] args) {
    SpringApplication.run(ChangeWatcherApplication.class, args);
  }

}

