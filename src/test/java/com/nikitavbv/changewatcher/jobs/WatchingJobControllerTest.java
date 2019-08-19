package com.nikitavbv.changewatcher.jobs;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nikitavbv.changewatcher.config.ApplicationProperties;
import com.nikitavbv.changewatcher.config.WatchingJobProperties;
import com.nikitavbv.changewatcher.user.ApplicationUserRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WatchingJobControllerTest {

  private WatchingJobController controller;

  @Mock private ApplicationUserRepository applicationUserRepository;
  @Mock private WatchingJobRepository watchingJobRepository;
  @Mock private ApplicationProperties applicationProperties;
  @Mock private WatchingJobProperties watchingJobProperties;
  @Mock private ExecutorService executorService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    controller = new WatchingJobController(
        applicationUserRepository,
        watchingJobRepository,
        applicationProperties,
        executorService
    );

    WatchingJob oldJob = mock(WatchingJob.class);
    when(oldJob.isTimeToRun()).thenReturn(false);

    WatchingJob jobToRun = mock(WatchingJob.class);
    when(jobToRun.isTimeToRun()).thenReturn(true);
    when(jobToRun.makeRunThread(any(WatchingJobRepository.class), anyString()))
        .thenReturn(mock(Thread.class));

    when(watchingJobRepository.findAll()).thenReturn(asList(
        oldJob,
        jobToRun
    ));
  }

  @Test
  public void runJobs() {
    controller.runJobs();
    verify(executorService, times(1)).submit(any(Thread.class));
  }

  @Test
  public void testCreateExecutor() {
    when(watchingJobProperties.getThreads()).thenReturn(8);

    WatchingJobController newController = new WatchingJobController(
        applicationUserRepository,
        watchingJobRepository,
        applicationProperties,
        watchingJobProperties
    );

    ThreadPoolExecutor executor = ((ThreadPoolExecutor) newController.getExecutorService());

    assertEquals(8, executor.getMaximumPoolSize());
  }
}
