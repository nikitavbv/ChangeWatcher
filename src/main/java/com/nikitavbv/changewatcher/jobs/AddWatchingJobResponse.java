package com.nikitavbv.changewatcher.jobs;

import java.util.List;

/**
 * Add watching job api response.
 *
 * @author Nikita Volobuev
 */
public class AddWatchingJobResponse {

  /** List of all watching jobs of this user. */
  private List<WatchingJob> jobs;
  /** The ID of new job which was just created. */
  private long newJobID;

  /** Construct add watching job response. */
  public AddWatchingJobResponse(long newJobID, List<WatchingJob> jobs) {
    this.jobs = jobs;
    this.newJobID = newJobID;
  }

  /** Returns all watching jobs on this account. */
  public List<WatchingJob> getJobs() {
    return jobs;
  }

  /** Returns id of newly created job. */
  public long getNewJobID() {
    return this.newJobID;
  }

}
