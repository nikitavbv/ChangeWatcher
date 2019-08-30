package com.nikitavbv.changewatcher.user;

import com.nikitavbv.changewatcher.jobs.WatchingJob;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

/**
 * Entity representing application user.
 *
 * @author Nikita Volobuev
 */
@Entity
public class ApplicationUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Email
  @Column(nullable = false)
  private String email;

  /** Watching jobs created by this user. */
  /* @OneToMany
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "job_id")
  )*/
  private final List<WatchingJob> jobs = new ArrayList<>();

  /** Add a new watching job to this user. */
  public void addJob(final WatchingJob job) {
    this.jobs.add(job);
  }

  /** Remove watching job from this user. */
  public void removeJob(final WatchingJob job) {
    this.jobs.remove(job);
  }

  /** Get list of jobs belonging to this user. */
  public List<WatchingJob> getJobs() {
    return this.jobs;
  }
}
