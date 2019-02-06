package com.nikitavbv.changewatcher.jobs;

import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.api.StatusOKResponse;
import com.nikitavbv.changewatcher.exceptions.PermissionDeniedException;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import com.nikitavbv.changewatcher.user.ApplicationUserRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(RouteConstants.JOBS_API)
public class WatchingJobController {

  private ApplicationUserRepository applicationUserRepository;
  private WatchingJobRepository watchingJobRepository;

  public WatchingJobController(ApplicationUserRepository applicationUserRepository,
                               WatchingJobRepository watchingJobRepository) {
    this.applicationUserRepository = applicationUserRepository;
    this.watchingJobRepository = watchingJobRepository;
  }

  @PostMapping
  public AddWatchingJobResponse addWatchingJob(HttpServletRequest req, @RequestBody WatchingJob job) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    job.setUser(user);
    watchingJobRepository.save(job);
    user.addJob(job);
    applicationUserRepository.save(user);
    return new AddWatchingJobResponse(user.getJobs());
  }

  @PostMapping("/{jobID}")
  public StatusOKResponse updateWatchingJob(HttpServletRequest req,
                                @PathVariable long jobID,
                                @RequestBody WatchingJob newJob) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    WatchingJob job = watchingJobRepository.findById(jobID).orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getId() != user.getId()) {
      throw new PermissionDeniedException("Cannot edit jobs owned by other users");
    }
    job.setTitle(newJob.getTitle());
    job.setUrl(newJob.getUrl());
    job.setWatchingInterval(newJob.getWatchingInterval());
    watchingJobRepository.save(job);
    return new StatusOKResponse();
  }

  @DeleteMapping("/{jobID}")
  public StatusOKResponse deleteWatchingJob(HttpServletRequest req, @PathVariable long jobID) {
    ApplicationUser user = applicationUserRepository.findByUsername(req.getRemoteUser());
    WatchingJob job = watchingJobRepository.findById(jobID).orElseThrow(WatchingJobNotFoundException::new);
    if (job.getUser().getId() != user.getId()) {
      throw new PermissionDeniedException("Cannot delete jobs owned by other users");
    }
    user.removeJob(job);
    applicationUserRepository.save(user);
    watchingJobRepository.delete(job);
    return new StatusOKResponse();
  }

}
