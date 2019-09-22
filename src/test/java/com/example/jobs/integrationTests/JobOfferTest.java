package com.example.jobs.integrationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class JobOfferTest {

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private JobOfferRepository repository;

  private JobOfferResource resource;

  private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

  private static final String GET_JOB_OFFERS_URL = "/jobmanagement/v1/joboffers";

  private static final String JOB_OFFER_URL = "/jobmanagement/v1/joboffer/";

  private static final String APPLICATIONS = "/applications";

  private List<JobOfferResource> jobOfferList;

  private JobOfferResource existingJobOffer;

  private JobApplicationResource existingJobApplication;

  @Before
  public void setUp() {
    resource = JobOfferResource.builder()
        .jobTitle("Dummy job 1")
        .jobDescription("Job desc")
        .location("Hyd")
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .startDate("2019-11-01")
        .vacancies(1)
        .build();

    existingJobOffer = JobOfferResource.builder()
        .id(UUID.fromString("44e96acb-a6e1-413e-be05-d49960c36da4"))
        .jobTitle("Lead Java Developer")
        .jobDescription("Must be experienced in Java and Spring")
        .location("Berlin")
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .startDate("2019-11-01")
        .vacancies(2)
        .numberOfApplications(1)
        .build();

    existingJobApplication = JobApplicationResource.builder()
        .id(UUID.fromString("ba326734-7f53-4719-9fe1-eafed9ce1295"))
        .candidateEmail("abc@jobs.com")
        .resumeText("First Candidate for the job")
        .applicationStatus(JobApplicationStatus.APPLIED)
        .build();

    jobOfferList = new ArrayList<>();
    jobOfferList.add(existingJobOffer);
  }

  @Test
  public void aFirst_getAllJobOffers_WithValidInput_ShouldReturnListOfJobOffers() throws Exception {
    mockMvc.perform(get(GET_JOB_OFFERS_URL)).andExpect(status().isOk());
  }

  @Test
  public void bSecond_createJobOffer_WithValidInput_ShouldReturnResourceWithStatusCreated() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(post(JOB_OFFER_URL).contentType(JSON_CONTENT_TYPE).content(objectMapper.writeValueAsString(resource)))
        .andExpect(status().isCreated()).andReturn().getResponse();

    JobOfferResource jobOfferResource = objectMapper.readValue(response.getContentAsString(), JobOfferResource.class);

    JobOffer jobOffer = repository.findById(jobOfferResource.getId());
    assertNotNull(jobOffer);
    assertEquals(resource.getJobTitle(), jobOffer.getJobTitle());
    assertEquals(resource.getJobDescription(), jobOffer.getJobDescription());
    assertEquals(resource.getLocation(), jobOffer.getLocation());
    assertEquals(resource.getStartDate(), jobOffer.getStartDate());
    assertEquals(resource.getJobOfferStatus(), jobOffer.getJobOfferStatus());
    assertEquals(resource.getVacancies(), jobOffer.getVacancies());
  }

  @Test
  public void cThird_getJobOfferById_WithValidInput_ShouldReturnTheJobOffer() throws Exception {
    mockMvc.perform(get(JOB_OFFER_URL + existingJobOffer.getId().toString())).andExpect(status().isOk());
  }

  @Test
  public void dFourth_getJobApplicationsByJobOfferId_WithValidInput_ShouldReturnListOfApplications() throws Exception {
    mockMvc.perform(
        get(JOB_OFFER_URL + existingJobOffer.getId().toString() + APPLICATIONS))
        .andExpect(status().isOk());
  }

}
