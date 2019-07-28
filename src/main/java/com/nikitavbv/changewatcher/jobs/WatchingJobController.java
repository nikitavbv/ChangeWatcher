package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.ApplicationProperties;
import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.api.StatusOkResponse;
import com.nikitavbv.changewatcher.security.PermissionDeniedException;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import com.nikitavbv.changewatcher.user.ApplicationUserRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API to create, update and delete change watching jobs.
 *
 * @author Nikita Volobuev
 */
@RestController
@RequestMapping(RouteConstants.JOBS_API)
public class WatchingJobController {

  /** Logger for this class. */
  private static final Logger LOG = Logger.getLogger(WatchingJobController.class.getName());

  /** Rate at which each page is rechecked. */
  private static final int WATCHING_RATE = 1000 * 60 * 10; // every 10 minutes
  /** Maximum number of threads to run page checks. */
  private static final int MAX_THREADS = 10;

  /** Directory to save screenshots to. */
  private static final String SCREENSHOTS_DIR = "screenshots/";

  /** User information is needed to manage their jobs. */
  private final ApplicationUserRepository userRepository;
  /** Job repository is required to manage jobs. */
  private final WatchingJobRepository jobRepository;
  /** Application properties are required to get application data/screenshots dir.*/
  private final ApplicationProperties properties;

  /** Executor service to run jobs. */
  private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

  /**
   * Creates controller for WatchingJob.
   *
   * @param userRepository repository with user details
   * @param jobRepository repository with watching job details
   * @param properties general application configuration
   */
  public WatchingJobController(final ApplicationUserRepository userRepository,
                               final WatchingJobRepository jobRepository,
                               final ApplicationProperties properties) {
    this.userRepository = userRepository;
    this.jobRepository = jobRepository;
    this.properties = properties;
  }

  @Scheduled(fixedRate = WATCHING_RATE)
  private void runJobs() {
    jobRepository.findAll().stream()
        .filter(WatchingJob::isTimeToRun)
        .forEach(job -> executorService.submit(
                job.makeRunThread(jobRepository, getScreenshotsDir())));
  }

  /**
   * Creates new watching job related to this user and saves it to repository.
   *
   * @return job id and job list
   */
  @PostMapping
  public AddWatchingJobResponse addWatchingJob(
          final HttpServletRequest req,
          @RequestBody final WatchingJob job
  ) {
    final ApplicationUser user = userRepository.findByUsername(req.getRemoteUser());
    job.setUser(user);
    jobRepository.save(job);
    return new AddWatchingJobResponse(job.getID(), user.getJobs());
  }

  /**
   * Updates job details.
   *
   * @throws PermissionDeniedException if user cannot access this job
   */
  @PostMapping("/{jobID}")
  public StatusOkResponse updateWatchingJob(final HttpServletRequest req,
                                            @PathVariable final long jobID,
                                            @RequestBody final WatchingJob newJob) {
    final ApplicationUser user = userRepository.findByUsername(req.getRemoteUser());
    final WatchingJob job = jobRepository
            .findById(jobID)
            .orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getUserID() != user.getUserID()) {
      throw new PermissionDeniedException("Cannot edit jobs owned by other users");
    }
    job.setTitle(newJob.getTitle());
    job.setUrl(newJob.getUrl());
    job.setWatchingInterval(newJob.getWatchingInterval());
    job.setWebhook(newJob.getWebhook());
    job.setPixelThreshold(newJob.getPixelThreshold());
    jobRepository.save(job);
    return new StatusOkResponse();
  }

  /**
   * Deletes job.
   *
   * @throws PermissionDeniedException if user cannot access this job
   */
  @DeleteMapping("/{jobID}")
  public StatusOkResponse deleteWatchingJob(final HttpServletRequest req,
                                            @PathVariable final long jobID) {
    final ApplicationUser user = userRepository.findByUsername(req.getRemoteUser());
    final WatchingJob job = jobRepository
            .findById(jobID)
            .orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getUserID() != user.getUserID()) {
      throw new PermissionDeniedException("Cannot delete jobs owned by other users");
    }
    user.removeJob(job);
    userRepository.save(user);
    jobRepository.delete(job);
    return new StatusOkResponse();
  }

  /**
   * Provides latest screenshot made by this job.
   *
   * @throws PermissionDeniedException if user does not have access to this job
   * @throws ScreenshotNotFoundException if no screenshots have been produced
   *     by this job yet
   */
  @GetMapping("/{jobID}/screenshot")
  public @ResponseBody byte[] getScreenshot(final HttpServletRequest req,
                                            @PathVariable final long jobID) {
    final ApplicationUser user = userRepository.findByUsername(req.getRemoteUser());
    final WatchingJob job = jobRepository
            .findById(jobID)
            .orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getUserID() != user.getUserID()) {
      throw new PermissionDeniedException("Cannot get screenshot of jobs owned by other users");
    }

    final File screenshotFile = job.getWebsiteScreenshotFile(getScreenshotsDir());
    if (!screenshotFile.exists()) {
      throw new ScreenshotNotFoundException();
    }

    try {
      return IOUtils.toByteArray(new FileInputStream(screenshotFile));
    } catch (IOException e) {
      throw new ScreenshotNotFoundException(e);
    }
  }

  private String getScreenshotsDir() {
    return properties.getDataDir() + SCREENSHOTS_DIR;
  }

}
