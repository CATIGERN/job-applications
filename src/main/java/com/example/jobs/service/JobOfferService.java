package com.example.jobs.service;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;

import java.util.List;
import java.util.UUID;

public interface JobOfferService {

  /**
   * Get all job offers by a given status. The number of results can be limited via the limit and offset fields.
   *
   * @param status The status of the job offers.
   * @param limit The number of results to be returned.
   * @param offset The number of results to be skipped initially.
   * @return The list of job offers.
   */
  List<JobOffer> getAllJobOffers(JobOfferStatus status, int limit, int offset);

  /**
   * Create a job offer.
   *
   * @param jobOffer The job offer to be created.
   * @return The created job offer.
   */
  JobOffer createJobOffer(JobOffer jobOffer);

  /**
   * Get a job offer by the given UUID. Throws Not Found Exception if the job offer Id doesn't exist.
   *
   * @param id The job offer UUID to be fetched.
   * @return The job offer for the given UUID.
   */
  JobOffer getJobOfferById(UUID id);

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
  List<JobApplication> getJobApplicationsByJobOfferId(UUID id, JobApplicationStatus status, int limit, int offset);

  /**
   * Mark a given job offer as INACTIVE.
   *
   * @param offer The job offer to be marked INACTIVE.
   */
  void markJobOfferInactive(JobOffer offer);

}
