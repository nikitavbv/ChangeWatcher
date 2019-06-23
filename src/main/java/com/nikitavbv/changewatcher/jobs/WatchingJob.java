package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.user.ApplicationUser;
import java.io.File;
import java.io.IOException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Entity to represent website change watching job.
 *
 * @author Nikita Volobuev
 */
@Entity
public class WatchingJob {

  /** Image format used for screenshot files. */
  /* default */ static final String SCREENSHOT_IMAGE_FORMAT = "png";

  /** Job id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Name of the watching job. */
  private String title;
  /** URL of webpage which is being watched. */
  private String url;
  /** Webhook to send post request to when change is detected. */
  private String webhook;
  /** Number of pixels to be changed to trigger listeners. */
  private long pixelDifferenceToTrigger;

  /** How often website is checked for changes. */
  private long watchingInterval;
  /** Timestamp of last web page fetch. */
  private long lastCheckTime;
  /** Number of pixel changes detected during the last check. */
  private long lastRunDifferentPixels;

  /** X coordinate of selection bounds. */
  private int selectionX;
  /** Y coordinate of selection bounds. */
  private int selectionY;
  /** Width of the selection region. */
  private int selectionWidth;
  /** Height of the selection region. */
  private int selectionHeight;

  /** User wh0 owns this job. */
  @ManyToOne
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "job_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private ApplicationUser user;

  /** Make new thread to run this watching job. */
  /* default */ Thread makeRunThread(WatchingJobRepository repository, String screenshotsDir) {
    return new WatchingJobThread(repository,this, screenshotsDir);
  }

  /**
   * Returns File where screenshot is saved to.
   *
   * @param screenshotsDir directory where screenshots are stored
   * @return file where screenshot is saved to.
   */
  /* default */ File getWebsiteScreenshotFile(String screenshotsDir) {
    return new File(screenshotsDir + "/" + getID() + "." + SCREENSHOT_IMAGE_FORMAT);
  }

  /**
   * Returns File where previous screenshot was saved to.
   *
   * @param screenshotsDir directory where screenshots are stored
   * @return file where previous screenshot was saved to.
   */
  /* default */ File getPrevWebsiteScreenshotFile(String screenshotsDir) {
    return new File(screenshotsDir + "/" + getID() + "_prev1" + "." + SCREENSHOT_IMAGE_FORMAT);
  }

  /* default */ void runWebhook() throws IOException {
    CloseableHttpClient httpClient = HttpClients.createMinimal();
    HttpGet httpGet = new HttpGet(webhook);
    httpClient.execute(httpGet);
    httpClient.close();
  }

  /* default */ boolean isTimeToRun() {
    return lastCheckTime + watchingInterval < System.currentTimeMillis();
  }

  /**
   * Get id of this job.
   * @return id of this job.
   */
  public long getID() {
    return id;
  }

  /**
   * Set user who owns this job.
   * @param user who owns this job
   */
  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  /**
   * Get job title.
   * @return job title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Set watching job title.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns URL which is being monitored.
   * @return job url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Set URL which is being monitored
   * @param url url to set
   */
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

  public long getLastCheckTime() {
    return this.lastCheckTime;
  }

  public void setLastCheckTime(long lastCheckTime) {
    this.lastCheckTime = lastCheckTime;
  }

  public long getLastRunDifferentPixels() {
    return this.lastRunDifferentPixels;
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
