package com.example.jobs.web.rest.controller;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.service.JobApplicationService;
import com.example.jobs.web.rest.assembler.JobApplicationAssembler;
import com.example.jobs.web.rest.resource.JobApplicationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/jobmanagement/v1")
public class JobApplicationController {

  @Autowired
  private JobApplicationAssembler jobApplicationAssembler;

  @Autowired
  private JobApplicationService jobApplicationService;

  private static final String APPLICATION_JSON_UTF8_VALUE = "application/json; charset=utf-8";

  /**
   * Create a job application for a given job offer UUID.
   *
   * @param jobOfferId The UUID of the job offer.
   * @param resource The job application to be created.
   * @return The created job application.
   */
  @PostMapping(value = "/application/{jobOfferId}", consumes = APPLICATION_JSON_UTF8_VALUE,
      produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<JobApplicationResource> createJobApplication(
      @PathVariable(name = "jobOfferId") String jobOfferId,
      @Valid @RequestBody JobApplicationResource resource
  ) {
    JobApplication application =
        jobApplicationService.createJobApplication(jobApplicationAssembler.toJobApplication(resource),
            UUID.fromString(jobOfferId));
    JobApplicationResource jobApplicationResource = jobApplicationAssembler.fromJobApplication(application);
    return new ResponseEntity<>(jobApplicationResource, HttpStatus.CREATED);
  }

  /**
   * Update the status of a job application.
   *
   * @param applicationId The UUID of the job application.
   * @param body The map containing the parameter status to be updated for the application.
   * @return The updated job application.
   */
  @PatchMapping(value = "/application/{applicationId}", consumes = APPLICATION_JSON_UTF8_VALUE,
      produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<JobApplicationResource> updateJobApplication(
      @PathVariable(name = "applicationId") String applicationId,
      @RequestBody Map<String, String> body
  ) {
    if (!body.containsKey("status")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status field is not present in the body.");
    }
    JobApplication application = jobApplicationService
        .updateJobApplication(UUID.fromString(applicationId), JobApplicationStatus.valueOf(body.get("status")));
    JobApplicationResource applicationResource = jobApplicationAssembler.fromJobApplication(application);
    return new ResponseEntity<>(applicationResource, HttpStatus.OK);
  }

  /**
   * Get a job application by its UUID.
   *
   * @param applicationId The UUID of the job application.
   * @return The job application for the given UUID.
   */
  @GetMapping(value = "/application/{applicationId}", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<JobApplicationResource> getJobApplicationById(
      @PathVariable(name = "applicationId") String applicationId
  ) {
    JobApplication application = jobApplicationService.getJobApplicationById(UUID.fromString(applicationId));
    JobApplicationResource resource = jobApplicationAssembler.fromJobApplication(application);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

}
