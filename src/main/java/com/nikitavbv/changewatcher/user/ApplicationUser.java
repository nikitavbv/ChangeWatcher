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

  /** User id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  /** User name. */
  private String username;
  /** User password (hashed). */
  private String password;
  /** Indicates if user is admin. */
  private boolean isAdmin = false;

  /** Watching jobs created by this user. */
  @OneToMany
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "job_id")
  )
  private List<WatchingJob> jobs = new ArrayList<>();

  /** Add a new watching job to this user. */
  public void addJob(WatchingJob job) {
    this.jobs.add(job);
  }

  /** Remove watching job from this user. */
  public void removeJob(WatchingJob job) {
    this.jobs.remove(job);
  }

  /** Get user id. */
  public long getId() {
    return id;
  }

  /** Get user name. */
  public String getUsername() {
    return username;
  }

  /** Set user name. */
  public void setUsername(String username) {
    this.username = username;
  }

  /** Get user password. */
  public String getPassword() {
    return password;
  }

  /** Set user password. */
  public void setPassword(String password) {
    this.password = password;
  }

  /** Return if this user is admin. */
  public boolean getIsAdmin() {
    return isAdmin;
  }

  /** Set admin status of this user. */
  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  /** Get list of jobs belonging to this user. */
  public List<WatchingJob> getJobs() {
    return this.jobs;
  }
}
