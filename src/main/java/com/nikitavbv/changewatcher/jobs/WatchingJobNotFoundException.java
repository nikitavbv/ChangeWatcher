package com.nikitavbv.changewatcher.jobs;

public class WatchingJobNotFoundException extends RuntimeException {

  public WatchingJobNotFoundException() {
    super("Watching job not found");
  }

}
