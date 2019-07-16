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

  /** Rate at which each page is rechecked. */
  private static final int WATCHING_RATE = 1000 * 60 * 10; // every 10 minutes
  /** Maximum number of threads to run page checks. */
  private static final int MAX_CHECKS_THREADS = 10;

  /** Directory to save screenshots to. */
  private static final String SCREENSHOTS_DIR = "screenshots/";

  /** User information is needed to manage their jobs. */
  private final ApplicationUserRepository applicationUserRepository;
  /** Job repository is required to manage jobs. */
  private final WatchingJobRepository watchingJobRepository;
  /** Application properties are required to get application data/screenshots dir.*/
  private final ApplicationProperties applicationProperties;

  /** Executor service to run jobs. */
  private ExecutorService executorService = Executors.newFixedThreadPool(MAX_CHECKS_THREADS);

  /**
   * Creates controller for WatchingJob.
   *
   * @param applicationUserRepository repository with user details
   * @param watchingJobRepository repository with watching job details
   * @param applicationProperties general application configuration
   */
  public WatchingJobController(ApplicationUserRepository applicationUserRepository,
                               WatchingJobRepository watchingJobRepository,
                               ApplicationProperties applicationProperties) {
    this.applicationUserRepository = applicationUserRepository;
    this.watchingJobRepository = watchingJobRepository;
    this.applicationProperties = applicationProperties;
  }

  @Scheduled(fixedRate = WATCHING_RATE)
  private void runJobs() {
    watchingJobRepository.findAll().stream()
        .filter(WatchingJob::isTimeToRun)
        .forEach(job -> {
          try {
            executorService.submit(job.makeRunThread(watchingJobRepository, getScreenshotsDir()));
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  /**
   * Creates new watching job related to this user and saves it to repository.
   *
   * @return job id and job list
   */
  @PostMapping
  public AddWatchingJobResponse addWatchingJob(
          HttpServletRequest req,
          @RequestBody WatchingJob job
  ) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    job.setUser(user);
    watchingJobRepository.save(job);
    return new AddWatchingJobResponse(job.getID(), user.getJobs());
  }

  /**
   * Updates job details.
   *
   * @throws PermissionDeniedException if user cannot access this job
   */
  @PostMapping("/{jobID}")
  public StatusOkResponse updateWatchingJob(HttpServletRequest req,
                                            @PathVariable long jobID,
                                            @RequestBody WatchingJob newJob) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    WatchingJob job = watchingJobRepository
            .findById(jobID)
            .orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getId() != user.getId()) {
      throw new PermissionDeniedException("Cannot edit jobs owned by other users");
    }
    job.setTitle(newJob.getTitle());
    job.setUrl(newJob.getUrl());
    job.setWatchingInterval(newJob.getWatchingInterval());
    job.setWebhook(newJob.getWebhook());
    job.setPixelDifferenceToTrigger(newJob.getPixelDifferenceToTrigger());
    watchingJobRepository.save(job);
    return new StatusOkResponse();
  }

  /**
   * Deletes job.
   *
   * @throws PermissionDeniedException if user cannot access this job
   */
  @DeleteMapping("/{jobID}")
  public StatusOkResponse deleteWatchingJob(HttpServletRequest req, @PathVariable long jobID) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    WatchingJob job = watchingJobRepository
            .findById(jobID)
            .orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getId() != user.getId()) {
      throw new PermissionDeniedException("Cannot delete jobs owned by other users");
    }
    user.removeJob(job);
    applicationUserRepository.save(user);
    watchingJobRepository.delete(job);
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
  public @ResponseBody byte[] getScreenshot(HttpServletRequest req, @PathVariable long jobID) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    WatchingJob job = watchingJobRepository
            .findById(jobID)
            .orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getId() != user.getId()) {
      throw new PermissionDeniedException("Cannot get screenshot of jobs owned by other users");
    }

    File screenshotFile = job.getWebsiteScreenshotFile(getScreenshotsDir());
    if (!screenshotFile.exists()) {
      throw new ScreenshotNotFoundException();
    }

    try {
      return IOUtils.toByteArray(new FileInputStream(screenshotFile));
    } catch (IOException e) {
      throw new ScreenshotNotFoundException();
    }
  }

  private String getScreenshotsDir() {
    return applicationProperties.getDataDir() + SCREENSHOTS_DIR;
  }

}
