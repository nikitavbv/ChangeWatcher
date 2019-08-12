package com.nikitavbv.changewatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Contains watching jobs properties defined by app.jobs prefix.
 *
 * @author Nikita Volobuev
 */
@SuppressWarnings("FieldCanBeLocal")
@ConfigurationProperties("app.jobs")
public class WatchingJobProperties {

  /** Maximum number of jobs running at the same time. */
  private int threads = 10;

  /** Returns maximum number of jobs running at the same time. */
  public int getThreads() {
    return threads;
  }
}
