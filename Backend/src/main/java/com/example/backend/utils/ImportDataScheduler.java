package com.example.backend.utils;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImportDataScheduler {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job importUserJob;

    @Scheduled(cron = "0 56 15 * * ?")
    public void runImportData () {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(importUserJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobRestartException | JobParametersInvalidException |
                 JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        }
    }
}
