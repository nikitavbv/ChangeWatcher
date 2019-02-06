package com.nikitavbv.changewatcher.user;

import com.nikitavbv.changewatcher.jobs.WatchingJob;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ApplicationUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String username;
  private String password;
  private boolean isAdmin = false;

  @OneToMany
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "job_id")
  )
  private List<WatchingJob> jobs = new ArrayList<>();

  public void addJob(WatchingJob job) {
    this.jobs.add(job);
  }

  public void removeJob(WatchingJob job) {
    this.jobs.remove(job);
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public List<WatchingJob> getJobs() {
    return this.jobs;
  }

}
