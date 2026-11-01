package com.batch.settlement.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

  private final JobOperator jobOperator;
  private final Job settlementJob;

  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void runJob() {

    String targetDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
            .minusDays(7)
            .toString();

    JobParameters jobParameters = new JobParametersBuilder()
            .addString("targetDate", targetDate)
            .toJobParameters();

    log.info("Start Scheduler! Starting settlement job.");

    try {
      log.info("Starting settlement job. targetDate = {}", targetDate);

      JobExecution execution = jobOperator.start(settlementJob, jobParameters);

      log.info("Job launched. execution = {}", execution);
    } catch (JobInstanceAlreadyCompleteException e) {
      log.info("Settlement already completed. targetDate={}", targetDate);
    } catch (JobExecutionAlreadyRunningException e) {
      log.warn("Settlement job is already running. targetDate={}", targetDate);
    } catch (InvalidJobParametersException | JobRestartException e) {
      log.error("Failed to launch settlement job. targetDate={}", targetDate, e);
    }
  }

}
