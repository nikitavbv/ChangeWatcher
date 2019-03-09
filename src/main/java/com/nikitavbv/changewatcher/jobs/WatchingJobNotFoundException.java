package com.nikitavbv.changewatcher.jobs;

/**
 * Thrown if requested watching job does not exist.
 *
 * @author Nikita Volobuev
 */
public class WatchingJobNotFoundException extends RuntimeException {

  public WatchingJobNotFoundException() {
    super("Watching job not found");
  }

}
