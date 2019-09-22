package com.example.jobs.service;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.enums.JobApplicationStatus;

import java.util.UUID;

public interface JobApplicationService {

  /**
   * Create a job application for a given job offer UUID. The corresponding job creation event is published to the
   * publisher.
   *
   * @param jobOfferId The UUID of the job offer.
   * @param application The job application to be created.
   * @return The created job application.
   */
  JobApplication createJobApplication(JobApplication application, UUID jobOfferId);

  /**
   * Update the status of a job application. The corresponding job update event is published to the publisher.
   *
   * @param id The UUID of the job application.
   * @param status The status to update for the application.
   * @return The updated job application.
   */
  JobApplication updateJobApplication(UUID id, JobApplicationStatus status);

  /**
   * Get a job application by its UUID.
   *
   * @param id The UUID of the job application.
   * @return The job application for the given UUID.
   */
  JobApplication getJobApplicationById(UUID id);

}
