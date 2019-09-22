package com.example.jobs.service.impl;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.PublishEvent;
import com.example.jobs.domain.enums.EventType;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.domain.enums.PublishStatus;
import com.example.jobs.notification.NotificationPublisher;
import com.example.jobs.repository.JobOfferRepository;
import com.example.jobs.service.JobOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class JobOfferServiceImpl implements JobOfferService {

  @Autowired
  private JobOfferRepository repository;

  @Autowired
  private NotificationPublisher notificationPublisher;

  /**
   * Get all job offers by a given status. The number of results can be limited via the limit and offset fields.
   *
   * @param status The status of the job offers.
   * @param limit The number of results to be returned.
   * @param offset The number of results to be skipped initially.
   * @return The list of job offers.
   */
  public List<JobOffer> getAllJobOffers(JobOfferStatus status, int limit, int offset) {
    return repository.findJobOffers(status.toString(), limit, offset);
  }

  /**
   * Create a job offer. The event is also published to the publisher.
   *
   * @param jobOffer The job offer to be created.
   * @return The created job offer.
   */
  @Transactional(rollbackOn = Exception.class)
  public JobOffer createJobOffer(JobOffer jobOffer) {
    if (repository.findByJobTitleIgnoreCase(jobOffer.getJobTitle()) != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Job offer with job title: " + jobOffer.getJobTitle() + " already exists.");
    }
    getCreatedJobOffer(jobOffer);
    jobOffer = repository.save(jobOffer);
    publishEventForJobOfferCreated(jobOffer, PublishStatus.CREATED);
    return jobOffer;
  }

  private void getCreatedJobOffer(JobOffer jobOffer) {
    jobOffer.setId(UUID.randomUUID());
    jobOffer.setJobOfferStatus(JobOfferStatus.ACTIVE);
    jobOffer.setCreatedTime(new Date());
    jobOffer.setUpdatedTime(new Date());
  }

  /**
   * Get a job offer by the given UUID. Throws Not Found Exception if the job offer Id doesn't exist.
   *
   * @param id The job offer UUID to be fetched.
   * @return The job offer for the given UUID.
   */
  public JobOffer getJobOfferById(UUID id) {
    JobOffer offer = repository.findById(id);
    if (offer == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job Offer found associated with the ID.");
    }
    return offer;
  }

  /**
   * Get all applications for a job offer by the application status. The results can be fetched via the limit and offset
   * parameters. Throws Not Found Exception if the job offer Id doesn't exist.
   *
   * @param id The UUID of the job offer.
   * @param status The status of the applications.
   * @param limit The number of results to be fetched.
   * @param offset The number of results to be skipped initially.
   * @return The list of job applications.
   */
  public List<JobApplication> getJobApplicationsByJobOfferId(UUID id, JobApplicationStatus status, int limit,
      int offset) {
    JobOffer jobOffer = getJobOfferById(id);
    return jobOffer.getApplications().stream()
        .filter(x -> x.getApplicationStatus().equals(status))
        .skip(offset)
        .limit(limit)
        .collect(Collectors.toList());
  }

  /**
   * Mark a given job offer as INACTIVE.
   *
   * @param offer The job offer to be marked INACTIVE.
   */
  @Transactional(rollbackOn = Exception.class)
  public void markJobOfferInactive(JobOffer offer) {
    offer.setJobOfferStatus(JobOfferStatus.INACTIVE);
    offer = repository.save(offer);
    publishEventForJobOfferCreated(offer, PublishStatus.UPDATED);
  }

  private void publishEventForJobOfferCreated(JobOffer offer, PublishStatus status) {
    PublishEvent event = PublishEvent.builder()
        .id(offer.getId())
        .status(status)
        .eventType(EventType.JOB_OFFER)
        .txnTime(offer.getCreatedTime())
        .eventData(offer.toString())
        .build();
    notificationPublisher.publishEvent(event);
  }

}
