package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.ApplicationProperties;
import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.api.StatusOKResponse;
import com.nikitavbv.changewatcher.security.PermissionDeniedException;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import com.nikitavbv.changewatcher.user.ApplicationUserRepository;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RouteConstants.JOBS_API)
public class WatchingJobController {

  private static final int WATCHING_RATE = 1000 * 60 * 10; // every 10 minutes
  private static final int MAX_CHECKS_THREADS = 10;

  private static final String SCREENSHOTS_DIR = "screenshots/";

  private ApplicationUserRepository applicationUserRepository;
  private WatchingJobRepository watchingJobRepository;
  private ApplicationProperties applicationProperties;

  private ExecutorService executorService = Executors.newFixedThreadPool(MAX_CHECKS_THREADS);

  public WatchingJobController(ApplicationUserRepository applicationUserRepository,
                               WatchingJobRepository watchingJobRepository,
                               ApplicationProperties applicationProperties) {
    this.applicationUserRepository = applicationUserRepository;
    this.watchingJobRepository = watchingJobRepository;
    this.applicationProperties = applicationProperties;
  }

  @Scheduled(fixedRate = WATCHING_RATE)
  public void runJobs() {
    watchingJobRepository.findAll().stream()
        .filter(WatchingJob::isTimeToRun)
        .forEach(job -> {
          try {
            executorService.submit(job.makeRunThread(watchingJobRepository, getScreenshotsDir()));
          } catch(Exception e) {
            e.printStackTrace();
          }
        });
  }

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

  @PostMapping("/{jobID}")
  public StatusOKResponse updateWatchingJob(HttpServletRequest req,
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
    return new StatusOKResponse();
  }

  @DeleteMapping("/{jobID}")
  public StatusOKResponse deleteWatchingJob(HttpServletRequest req, @PathVariable long jobID) {
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
    return new StatusOKResponse();
  }

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
    } catch(IOException e) {
      throw new ScreenshotNotFoundException();
    }
  }

  private String getScreenshotsDir() {
    return applicationProperties.getDataDir() + SCREENSHOTS_DIR;
  }

}
