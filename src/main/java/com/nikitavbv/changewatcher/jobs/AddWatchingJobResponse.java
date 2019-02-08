package com.nikitavbv.changewatcher.jobs;

import java.util.List;

public class AddWatchingJobResponse {

  private List<WatchingJob> jobs;
  private long newJobID;

  public AddWatchingJobResponse(long newJobID, List<WatchingJob> jobs) {
    this.jobs = jobs;
    this.newJobID = newJobID;
  }

  public List<WatchingJob> getJobs() {
    return jobs;
  }

  public long getNewJobID() {
    return this.newJobID;
  }

}
