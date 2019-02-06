package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.user.ApplicationUser;

import javax.persistence.*;

@Entity
public class WatchingJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String title;
  private String url;

  private long watchingInterval;
  private long lastCheckTime;

  @ManyToOne
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "job_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private ApplicationUser user;

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public long getWatchingInterval() {
    return watchingInterval;
  }

  public void setWatchingInterval(long watchingInterval) {
    this.watchingInterval = watchingInterval;
  }

  ApplicationUser getUser() {
    return user;
  }

}
