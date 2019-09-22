package com.example.jobs.service.impl;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.PublishEvent;
import com.example.jobs.domain.enums.EventType;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.domain.enums.PublishStatus;
import com.example.jobs.notification.NotificationPublisher;
import com.example.jobs.repository.JobApplicationRepository;
import com.example.jobs.service.JobApplicationService;
import com.example.jobs.service.JobOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

  @Autowired
  private JobOfferService jobOfferService;

  @Autowired
  private JobApplicationRepository repository;

  @Autowired
  private NotificationPublisher notificationPublisher;

  /**
   * Create a job application for a given job offer UUID. The corresponding job creation event is published to the
   * publisher.
   *
   * @param jobOfferId The UUID of the job offer.
   * @param application The job application to be created.
   * @return The created job application.
   */
  @Transactional(rollbackOn = Exception.class)
  public JobApplication createJobApplication(JobApplication application, UUID jobOfferId) {
    JobOffer jobOffer = jobOfferService.getJobOfferById(jobOfferId);
    if (jobOffer.getJobOfferStatus() == JobOfferStatus.INACTIVE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job offer has no vacancies");
    }
    checkForDuplicateJobApplication(jobOffer, application);
    getCreatedJobApplication(jobOffer, application);
    application = repository.save(application);
    publishEventForJobApplication(application, PublishStatus.CREATED);
    return application;
  }

  private void checkForDuplicateJobApplication(JobOffer jobOffer, JobApplication application) {
    jobOffer.getApplications()
        .stream()
        .filter(x -> x.getCandidateEmail().equals(application.getCandidateEmail()))
        .findFirst()
        .ifPresent(x -> {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Job Application for the given email already exists.");
        });
  }

  private void getCreatedJobApplication(JobOffer jobOffer, JobApplication application) {
    application.setId(UUID.randomUUID());
    application.setApplicationStatus(JobApplicationStatus.APPLIED);
    application.setJobOffer(jobOffer);
    application.setCreatedTime(new Date());
    application.setUpdatedTime(new Date());
  }

  /**
   * Get a job application by its UUID.
   *
   * @param id The UUID of the job application.
   * @return The job application for the given UUID.
   */
  public JobApplication getJobApplicationById(UUID id) {
    JobApplication application = repository.findById(id);
    if (application == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job Application found associated with the ID.");
    }
    return application;
  }

  /**
   * Update the status of a job application. The corresponding job update event is published to the publisher.
   *
   * @param id The UUID of the job application.
   * @param status The status to update for the application.
   * @return The updated job application.
   */
  public JobApplication updateJobApplication(UUID id, JobApplicationStatus status) {
    JobApplication application = getJobApplicationById(id);
    if (application.getJobOffer().getJobOfferStatus() == JobOfferStatus.INACTIVE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job offer is expired.");
    }
    application.setApplicationStatus(status);
    application.setUpdatedTime(new Date());
    application = repository.save(application);
    publishEventForJobApplication(application, PublishStatus.UPDATED);
    if (status == JobApplicationStatus.HIRED && application.getJobOffer().getApplications().stream()
        .filter(x -> x.getApplicationStatus().equals(JobApplicationStatus.HIRED)).count() ==
        application.getJobOffer().getVacancies()) {
      // Mark job as Inactive since all vacancies are filled.
      jobOfferService.markJobOfferInactive(application.getJobOffer());
    }
    return application;
  }

  private void publishEventForJobApplication(JobApplication application, PublishStatus status) {
    PublishEvent event = PublishEvent.builder()
        .id(application.getId())
        .status(status)
        .eventType(EventType.JOB_APPLICATION)
        .txnTime(application.getCreatedTime())
        .eventData(application.toString())
        .build();
    notificationPublisher.publishEvent(event);
  }
}
