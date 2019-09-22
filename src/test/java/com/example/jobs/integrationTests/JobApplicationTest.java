package com.example.jobs.integrationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.repository.JobApplicationRepository;
import com.example.jobs.repository.JobOfferRepository;
import com.example.jobs.web.rest.resource.JobApplicationResource;
import com.example.jobs.web.rest.resource.JobOfferResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class JobApplicationTest {

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private JobApplicationRepository jobApplicationRepository;

  @Autowired
  private JobOfferRepository jobOfferRepository;

  private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

  private static final String APPLICATION_URL = "/jobmanagement/v1/application/";

  private JobApplicationResource existingJobApplication;

  private JobApplicationResource resource;

  private JobOfferResource jobOfferResource;

  private String existingJobOfferId = "44e96acb-a6e1-413e-be05-d49960c36da4";

  @Before
  public void setUp() {
    existingJobApplication = JobApplicationResource.builder()
        .id(UUID.fromString("ba326734-7f53-4719-9fe1-eafed9ce1295"))
        .candidateEmail("abc@jobs.com")
        .resumeText("First Candidate for the job")
        .applicationStatus(JobApplicationStatus.APPLIED)
        .build();

    resource = JobApplicationResource.builder()
        .applicationStatus(JobApplicationStatus.INVITED)
        .candidateEmail("applicant@jobs.com")
        .resumeText("Dummy resume")
        .build();

    jobOfferResource = JobOfferResource.builder()
        .jobTitle("Dummy job")
        .jobDescription("Job desc")
        .location("Hyd")
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .startDate("2019-11-01")
        .vacancies(1)
        .build();
  }

  @Test
  public void aFirst_createJobApplication_WithValidInput_ShouldCreateJobApplication() throws Exception {
    MockHttpServletResponse response =
        mockMvc.perform(post(APPLICATION_URL + existingJobOfferId).contentType(JSON_CONTENT_TYPE)
            .content(objectMapper.writeValueAsString(resource)))
            .andExpect(status().isCreated()).andReturn().getResponse();

    JobApplication application = objectMapper.readValue(response.getContentAsString(), JobApplication.class);
    JobApplication storedApplication = jobApplicationRepository.findById(application.getId());

    assertNotNull(storedApplication);
    assertEquals(application.getId(), storedApplication.getId());
    assertEquals(application.getCandidateEmail(), storedApplication.getCandidateEmail());
    assertEquals(application.getResumeText(), storedApplication.getResumeText());
    assertEquals(application.getApplicationStatus(), storedApplication.getApplicationStatus());
  }

  @Test
  public void bSecond_getJobApplicationById_WithValidInput_ShouldReturnTheApplicationDetails() throws Exception {
    mockMvc.perform(get(APPLICATION_URL + existingJobApplication.getId().toString())).andExpect(status().isOk());
  }

  @Test
  public void cThird_updateJobApplication_WithValidInput_ShouldUpdateTheApplication() throws Exception {
    Map<String, String> map = new HashMap<>();
    map.put("status", "INVITED");

    mockMvc.perform(patch(APPLICATION_URL + existingJobApplication.getId().toString()).contentType(JSON_CONTENT_TYPE)
        .content(objectMapper.writeValueAsString(map))).andExpect(status().isOk());

    JobApplication application = jobApplicationRepository.findById(existingJobApplication.getId());
    assertEquals(JobApplicationStatus.INVITED, application.getApplicationStatus());
  }

  @Test
  public void dFourth_updateJobApplication_WithAllVacanciesFilled_ShouldMarkJobOfferInactive() throws Exception {
    // Create job offer.
    MockHttpServletResponse response = mockMvc.perform(post("/jobmanagement/v1/joboffer/").contentType(JSON_CONTENT_TYPE)
        .content(objectMapper.writeValueAsString(jobOfferResource))).andExpect(status().isCreated()).andReturn()
        .getResponse();

    JobOfferResource createdJobOffer = objectMapper.readValue(response.getContentAsString(), JobOfferResource.class);
    JobOffer savedJobOffer = jobOfferRepository.findById(createdJobOffer.getId());
    assertEquals(JobOfferStatus.ACTIVE, savedJobOffer.getJobOfferStatus());

    // Apply for Job offer.
    response = mockMvc.perform(post(APPLICATION_URL + createdJobOffer.getId().toString()).contentType(JSON_CONTENT_TYPE)
        .content(objectMapper.writeValueAsString(resource)))
        .andExpect(status().isCreated()).andReturn().getResponse();

    JobApplicationResource createdJobApplication =
        objectMapper.readValue(response.getContentAsString(), JobApplicationResource.class);

    JobApplication application = jobApplicationRepository.findById(createdJobApplication.getId());
    assertEquals(JobApplicationStatus.APPLIED, application.getApplicationStatus());

    // Update job application to HIRED.
    Map<String, String> map = new HashMap<>();
    map.put("status", "HIRED");

    mockMvc.perform(patch(APPLICATION_URL + createdJobApplication.getId().toString()).contentType(JSON_CONTENT_TYPE)
        .content(objectMapper.writeValueAsString(map))).andExpect(status().isOk());

    application = jobApplicationRepository.findById(createdJobApplication.getId());
    assertEquals(JobApplicationStatus.HIRED, application.getApplicationStatus());

    savedJobOffer = jobOfferRepository.findById(createdJobOffer.getId());
    assertEquals(JobOfferStatus.INACTIVE, savedJobOffer.getJobOfferStatus());
  }


}
