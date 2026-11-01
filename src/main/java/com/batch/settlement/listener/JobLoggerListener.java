package com.batch.settlement.listener;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobLoggerListener {

  private static final String START_MESSAGE = "Batch job started: {}";
  private static final String END_MESSAGE = "Batch job completed: {} (status: {})";

  @BeforeJob
  public void beforeJob(JobExecution jobExecution) {
    log.info("=================================");
    log.info(START_MESSAGE, jobExecution.getJobInstance().getJobName());
    log.info("=================================");
  }

  @AfterJob
  public void afterJob(JobExecution jobExecution){
    String jobName = jobExecution.getJobInstance().getJobName();
    BatchStatus status = jobExecution.getStatus();

    log.info("=================================");
    log.info(END_MESSAGE, jobName, status);

    if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
      long duration = Duration.between(
              jobExecution.getStartTime(),
              jobExecution.getEndTime()
      ).toMillis();

      log.info("Execution time: {} ms", duration);
    }

    if(status == BatchStatus.FAILED){
      log.error("Batch job failed: {}", jobName);
    }

    log.info("=================================");
  }
}
