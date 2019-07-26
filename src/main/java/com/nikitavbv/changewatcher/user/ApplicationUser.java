package com.nikitavbv.changewatcher.user;

import com.nikitavbv.changewatcher.jobs.WatchingJob;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * Entity representing application user.
 *
 * @author Nikita Volobuev
 */
@Entity
public class ApplicationUser {

  /** User userID. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long userID;
  /** User name. */
  private String username;
  /** User password (hashed). */
  private String password;
  /** Indicates if user is admin. */
  private boolean admin;

  /** Watching jobs created by this user. */
  @OneToMany
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "job_id")
  )
  private final List<WatchingJob> jobs = new ArrayList<>();

  /** Add a new watching job to this user. */
  public void addJob(final WatchingJob job) {
    this.jobs.add(job);
  }

  /** Remove watching job from this user. */
  public void removeJob(final WatchingJob job) {
    this.jobs.remove(job);
  }

  /** Get user userID. */
  public long getUserID() {
    return userID;
  }

  /** Get user name. */
  public String getUsername() {
    return username;
  }

  /** Set user name. */
  public void setUsername(final String username) {
    this.username = username;
  }

  /** Get user password. */
  public String getPassword() {
    return password;
  }

  /** Set user password. */
  public void setPassword(final String password) {
    this.password = password;
  }

  /** Return if this user is admin. */
  public boolean isAdmin() {
    return admin;
  }

  /** Set admin status of this user. */
  public void setIsAdmin(final boolean isAdmin) {
    this.admin = isAdmin;
  }

  /** Get list of jobs belonging to this user. */
  public List<WatchingJob> getJobs() {
    return this.jobs;
  }
}
