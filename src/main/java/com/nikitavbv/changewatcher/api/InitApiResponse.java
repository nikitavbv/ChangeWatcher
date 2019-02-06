package com.nikitavbv.changewatcher.api;

import com.nikitavbv.changewatcher.jobs.WatchingJob;

import java.util.List;

public class InitApiResponse {

  private List<WatchingJob> jobs;

  public InitApiResponse(List<WatchingJob> jobs) {
    this.jobs = jobs;
  }

  public List<WatchingJob> getJobs() {
    return jobs;
  }

}
