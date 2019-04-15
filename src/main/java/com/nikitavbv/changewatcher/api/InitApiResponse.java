package com.nikitavbv.changewatcher.api;

import com.nikitavbv.changewatcher.jobs.WatchingJob;

import java.util.List;

/**
 * Response of init api call.
 *
 * @author Nikita Volobuev
 */
public class InitApiResponse {

  /** List of user's watching jobs. */
  private List<WatchingJob> jobs;

  /**
   * Construct init api response.
   *
   * @param jobs list of watching jobs of user performing the request.
   */
  public InitApiResponse(List<WatchingJob> jobs) {
    this.jobs = jobs;
  }

  /** Return list of watching jobs. */
  public List<WatchingJob> getJobs() {
    return jobs;
  }

}
