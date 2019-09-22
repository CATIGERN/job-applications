package com.example.jobs.web.rest.controller;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.service.JobOfferService;
import com.example.jobs.web.rest.assembler.JobApplicationAssembler;
import com.example.jobs.web.rest.assembler.JobOfferAssembler;
import com.example.jobs.web.rest.resource.JobApplicationResource;
import com.example.jobs.web.rest.resource.JobOfferResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/jobmanagement/v1")
public class JobOfferController {

  @Autowired
  private JobOfferService jobOfferService;

  @Autowired
  private JobOfferAssembler jobOfferAssembler;

  @Autowired
  private JobApplicationAssembler jobApplicationAssembler;

  private static final String APPLICATION_JSON_UTF8_VALUE = "application/json; charset=utf-8";

  /**
   * Get all job offers by a given status. The number of results can be limited via the limit and offset fields.
   *
   * @param status The status of the job offers.
   * @param limit The number of results to be returned.
   * @param offset The number of results to be skipped initially.
   * @return The list of job offers.
   */
  @GetMapping(value = "/joboffers", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<JobOfferResource>> getAllJobOffers(
      @RequestParam(name = "status", defaultValue = "ACTIVE", required = false) JobOfferStatus status,
      @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
      @RequestParam(name = "offset", defaultValue = "0", required = false) int offset
  ) {
    List<JobOffer> jobOffers = jobOfferService.getAllJobOffers(status, limit, offset);
    List<JobOfferResource> resourceList = jobOfferAssembler.fromJobOfferList(jobOffers);
    return new ResponseEntity<>(resourceList, HttpStatus.OK);
  }

  /**
   * Create a job offer.
   *
   * @param jobOfferResource The job offer to be created.
   * @return The created job offer.
   */
  @PostMapping(value = "/joboffer", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<JobOfferResource> createJobOffer(
      @Valid @RequestBody JobOfferResource jobOfferResource
  ) {
    JobOffer jobOffer = jobOfferService.createJobOffer(jobOfferAssembler.toJobOffer(jobOfferResource));
    JobOfferResource resource = jobOfferAssembler.fromJobOffer(jobOffer);
    return new ResponseEntity<>(resource, HttpStatus.CREATED);
  }

  /**
   * Get a job offer by the given UUID.
   *
   * @param jobOfferId The job offer UUID to be fetched.
   * @return The job offer for the given UUID.
   */
  @GetMapping(value = "/joboffer/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<JobOfferResource> getJobOfferById(
      @PathVariable(name = "id") String jobOfferId
  ) {
    JobOffer jobOffer = jobOfferService.getJobOfferById(UUID.fromString(jobOfferId));
    JobOfferResource resource = jobOfferAssembler.fromJobOffer(jobOffer);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  /**
   * Get all applications for a job offer by the application status. The results can be fetched via the limit and offset
   * parameters.
   *
   * @param jobOfferId The UUID of the job offer.
   * @param applicationStatus The status of the applications.
   * @param limit The number of results to be fetched.
   * @param offset The number of results to be skipped initially.
   * @return The list of job applications.
   */
  @GetMapping(value = "/joboffer/{jobOfferId}/applications", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<JobApplicationResource>> getJobApplicationsByJobOfferId(
      @PathVariable(name = "jobOfferId") String jobOfferId,
      @RequestParam(name = "applicationStatus", required = false, defaultValue = "APPLIED")
          JobApplicationStatus applicationStatus,
      @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset
  ) {
    List<JobApplication> applications = jobOfferService
        .getJobApplicationsByJobOfferId(UUID.fromString(jobOfferId), applicationStatus, limit, offset);
    List<JobApplicationResource> resourceList = jobApplicationAssembler.fromJobApplicationList(applications);
    return new ResponseEntity<>(resourceList, HttpStatus.OK);
  }
}
