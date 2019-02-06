package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.user.ApplicationUser;

import javax.persistence.*;
import java.io.File;

@Entity
public class WatchingJob {

  private static final String SCREENSHOT_IMAGE_FORMAT = "png";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String title;
  private String url;

  private long watchingInterval;
  private long lastCheckTime;
  private long lastRunDifferentPixels;

  @ManyToOne
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "job_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private ApplicationUser user;

  Thread makeRunThread(String screenshotsDir) {
    return new WatchingJobThread(this, screenshotsDir);
  }

  File getWebsiteScreenshotFile(String screenshotsDir) {
    return new File(screenshotsDir + "/" + getID() + "." + SCREENSHOT_IMAGE_FORMAT);
  }

  File getPrevWebsiteScreenshotFile(String screenshotsDir) {
    return new File(screenshotsDir + "/" + getID() + "_prev1" + "." + SCREENSHOT_IMAGE_FORMAT);
  }

  boolean isTimeToRun() {
    return lastCheckTime + watchingInterval < System.currentTimeMillis();
  }

  long getID() {
    return id;
  }

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

  public void setLastRunDifferentPixels(long differentPixels) {
    this.lastRunDifferentPixels = differentPixels;
  }

}
