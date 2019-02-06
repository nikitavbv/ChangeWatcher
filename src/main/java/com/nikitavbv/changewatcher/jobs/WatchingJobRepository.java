package com.nikitavbv.changewatcher.jobs;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchingJobRepository extends JpaRepository<WatchingJob, Long> {
}
