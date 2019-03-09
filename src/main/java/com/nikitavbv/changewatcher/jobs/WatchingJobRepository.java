package com.nikitavbv.changewatcher.jobs;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to store watching jobs.
 *
 * @author Nikita Volobuev
 */
public interface WatchingJobRepository extends JpaRepository<WatchingJob, Long> {
}
