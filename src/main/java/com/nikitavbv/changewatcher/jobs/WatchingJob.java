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
  /* default */ static final String SCREENSHOT_FORMAT = "png";

  /** Job jobID. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long jobID;

  /** Name of the watching job. */
  private String title;
  /** URL of webpage which is being watched. */
  private String url;
  /** Webhook to send post request to when change is detected. */
  private String webhook;
  /** Number of pixels to be changed to trigger listeners. */
  private long pixelThreshold;

  /** How often website is checked for changes. */
  private long watchingInterval;
  /** Timestamp of last web page fetch. */
  private long lastCheckTime;
  /** Number of pixel changes detected during the last check. */
  private long lastRunDifference;

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
  /* default */ Thread makeRunThread(final WatchingJobRepository repository, final String screenshotsDir) {
    return new WatchingJobThread(repository,this, screenshotsDir);
  }

  /**
   * Returns File where screenshot is saved to.
   *
   * @param screenshotsDir directory where screenshots are stored
   * @return file where screenshot is saved to.
   */
  /* default */ File getWebsiteScreenshotFile(final String screenshotsDir) {
    return new File(screenshotsDir + "/" + getID() + "." + SCREENSHOT_FORMAT);
  }

  /**
   * Returns File where previous screenshot was saved to.
   *
   * @param screenshotsDir directory where screenshots are stored
   * @return file where previous screenshot was saved to.
   */
  /* default */ File getPrevWebsiteScreenshotFile(final String screenshotsDir) {
    return new File(screenshotsDir + "/" + getID() + "_prev1" + "." + SCREENSHOT_FORMAT);
  }

  /* default */ void runWebhook() throws IOException {
    final CloseableHttpClient httpClient = HttpClients.createMinimal();
    final HttpGet httpGet = new HttpGet(webhook);
    httpClient.execute(httpGet);
    httpClient.close();
  }

  /* default */ boolean isTimeToRun() {
    return lastCheckTime + watchingInterval < System.currentTimeMillis();
  }

  /**
   * Get jobID of this job.
   * @return jobID of this job.
   */
  public long getID() {
    return jobID;
  }

  /**
   * Set user who owns this job.
   * @param user who owns this job
   */
  public void setUser(final ApplicationUser user) {
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
  public void setTitle(final String title) {
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
   * Set URL which is being monitored.
   * @param url url to set
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * Get this job notification webhook url.
   * @return
   */
  public String getWebhook() {
    return this.webhook;
  }

  /** Set job notification webhook url. */
  public void setWebhook(final String webhook) {
    this.webhook = webhook;
  }

  /**
   * Get job watching interval, i.e. how often this gets rechecked.
   *
   * @return watching interval (milliseconds)
   */
  public long getWatchingInterval() {
    return watchingInterval;
  }

  /**
   * Set job watching interval, i.e. how often webpage gets rechecked.
   *
   * @param watchingInterval milliseconds between page checks
   */
  public void setWatchingInterval(final long watchingInterval) {
    this.watchingInterval = watchingInterval;
  }

  /**
   * Get User who owns this job.
   * @return job owner
   */
  /* default */ ApplicationUser getUser() {
    return user;
  }

  /**
   * Get timestamp of last check.
   *
   * @return timestamp of last check
   */
  public long getLastCheckTime() {
    return this.lastCheckTime;
  }

  /** Set timestamp of last check. */
  public void setLastCheckTime(final long lastCheckTime) {
    this.lastCheckTime = lastCheckTime;
  }

  /**
   * Get total number of pixels which were different during last check.
   *
   * @return total different pixels
   */
  public long getLastRunDifference() {
    return this.lastRunDifference;
  }

  /**
   * Set total number of pixels which were different during last check.
   *
   * @param differentPixels total different pixels
   */
  public void setLastRunDifference(final long differentPixels) {
    this.lastRunDifference = differentPixels;
  }

  /**
   * Get total number of pixels required for notification to be triggered.
   *
   * @return number of pixels to trigger notification
   */
  public long getPixelThreshold() {
    return this.pixelThreshold;
  }

  /**
   * Set total number of pixles required for notification to be triggered.
   *
   * @param pixelThreshold number of pixels to trigger notification
   */
  public void setPixelThreshold(final long pixelThreshold) {
    this.pixelThreshold = pixelThreshold;
  }

  /**
   * Returns x coordinate of top left corner of region selected for tracking.
   * @return x coordinate of top left corner
   */
  public int getSelectionX() {
    return selectionX;
  }

  /**
   * Set x coordinate of top left corner of region selected for tracking.
   *
   * @param selectionX x coordinate of top left corner
   */
  public void setSelectionX(final int selectionX) {
    this.selectionX = selectionX;
  }

  /**
   * Returns y coordinate of top left corner of region selected for tracking.
   * @return y coordinate of top left corner
   */
  public int getSelectionY() {
    return selectionY;
  }

  /**
   * Set y coordinate of top left corner of region selected for tracking.
   *
   * @param selectionY y coordinate of top left corner
   */
  public void setSelectionY(final int selectionY) {
    this.selectionY = selectionY;
  }

  /**
   * Returns width of selected region for tracking.
   *
   * @return width of selected region for tracking
   */
  public int getSelectionWidth() {
    return selectionWidth;
  }

  /**
   * Sets width of selected region for tracking.
   *
   * @param selectionWidth width of selected region for tracking
   */
  public void setSelectionWidth(final int selectionWidth) {
    this.selectionWidth = selectionWidth;
  }

  /**
   * Returns height of selected region for tracking.
   *
   * @return height of selected region for tracking
   */
  public int getSelectionHeight() {
    return selectionHeight;
  }

  /**
   * Sets height of selected region for tracking.
   *
   * @param selectionHeight returns height of selected region for tracking
   */
  public void setSelectionHeight(final int selectionHeight) {
    this.selectionHeight = selectionHeight;
  }
}
