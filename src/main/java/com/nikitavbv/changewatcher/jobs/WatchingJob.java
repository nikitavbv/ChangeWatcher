package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.user.ApplicationUser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;

@Entity
public class WatchingJob {

  static final String SCREENSHOT_IMAGE_FORMAT = "png";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String title;
  private String url;
  private String webhook; // for notifications
  private long pixelDifferenceToTrigger;

  private long watchingInterval;
  private long lastCheckTime;
  private long lastRunDifferentPixels;

  private int selectionX;
  private int selectionY;
  private int selectionWidth;
  private int selectionHeight;

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

  void runWebhook() throws IOException {
    CloseableHttpClient httpClient = HttpClients.createMinimal();
    HttpGet httpGet = new HttpGet(webhook);
    httpClient.execute(httpGet);
    httpClient.close();
  }

  boolean isTimeToRun() {
    return lastCheckTime + watchingInterval < System.currentTimeMillis();
  }

  public long getID() {
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

  public String getWebhook() {
    return this.webhook;
  }

  public void setWebhook(String webhook) {
    this.webhook = webhook;
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

  public long getPixelDifferenceToTrigger() {
    return this.pixelDifferenceToTrigger;
  }

  public void setPixelDifferenceToTrigger(long pixelDifferenceToTrigger) {
    this.pixelDifferenceToTrigger = pixelDifferenceToTrigger;
  }

  public int getSelectionX() {
    return selectionX;
  }

  public void setSelectionX(int selectionX) {
    this.selectionX = selectionX;
  }

  public int getSelectionY() {
    return selectionY;
  }

  public void setSelectionY(int selectionY) {
    this.selectionY = selectionY;
  }

  public int getSelectionWidth() {
    return selectionWidth;
  }

  public void setSelectionWidth(int selectionWidth) {
    this.selectionWidth = selectionWidth;
  }

  public int getSelectionHeight() {
    return selectionHeight;
  }

  public void setSelectionHeight(int selectionHeight) {
    this.selectionHeight = selectionHeight;
  }
}
