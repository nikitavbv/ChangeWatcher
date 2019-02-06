package com.nikitavbv.changewatcher.jobs;

import java.util.List;

public class AddWatchingJobResponse {

  private List<WatchingJob> jobs;

  public AddWatchingJobResponse(List<WatchingJob> jobs) {
    this.jobs = jobs;
  }

}
