package com.example.jobs.web.rest.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.service.JobOfferService;
import com.example.jobs.web.rest.assembler.JobApplicationAssembler;
import com.example.jobs.web.rest.assembler.JobOfferAssembler;
import com.example.jobs.web.rest.resource.JobOfferResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class JobOfferControllerTest {

  @Mock
  private JobOfferService jobOfferService;

  @Mock
  private JobOfferAssembler jobOfferAssembler;

  @Mock
  private JobApplicationAssembler jobApplicationAssembler;

  @InjectMocks
  private JobOfferController controller;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

  private static final String GET_JOB_OFFERS_URL = "/jobmanagement/v1/joboffers";

  private static final String JOB_OFFER_URL = "/jobmanagement/v1/joboffer/";

  private static final String APPLICATIONS = "/applications";

  private JobOfferResource resource;

  @Before
  public void setUp() {
    mockMvc = standaloneSetup(controller).build();

    resource = JobOfferResource.builder()
        .jobTitle("Dummy job")
        .jobDescription("Job desc")
        .location("Hyd")
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .startDate("2019-11-01")
        .vacancies(1)
        .build();
  }

  @Test
  public void getAllJobOffers_WithValidInput_ShouldReturnListOfJobOffers() throws Exception {
    List<JobOffer> jobOffers = Arrays.asList(new JobOffer());
    List<JobOfferResource> jobOfferResourceList = Arrays.asList(new JobOfferResource());
    when(jobOfferService.getAllJobOffers(JobOfferStatus.ACTIVE, 10, 0)).thenReturn(jobOffers);
    when(jobOfferAssembler.fromJobOfferList(jobOffers)).thenReturn(jobOfferResourceList);

    mockMvc.perform(get(GET_JOB_OFFERS_URL)).andExpect(status().isOk());

    verify(jobOfferService, times(1)).getAllJobOffers(JobOfferStatus.ACTIVE, 10, 0);
    verify(jobOfferAssembler, times(1)).fromJobOfferList(jobOffers);
  }

  @Test
  public void createJobOffer_WithValidInput_ShouldReturnResourceWithStatusCreated() throws Exception {
    JobOffer jobOffer = new JobOffer();
    when(jobOfferAssembler.toJobOffer(resource)).thenReturn(jobOffer);
    when(jobOfferService.createJobOffer(jobOffer)).thenReturn(jobOffer);
    when(jobOfferAssembler.fromJobOffer(jobOffer)).thenReturn(resource);

    mockMvc.perform(
        post(JOB_OFFER_URL).contentType(JSON_CONTENT_TYPE).content(objectMapper.writeValueAsString(resource)))
        .andExpect(status().isCreated());

    verify(jobOfferAssembler, times(1)).toJobOffer(resource);
    verify(jobOfferService, times(1)).createJobOffer(jobOffer);
    verify(jobOfferAssembler, times(1)).fromJobOffer(jobOffer);
  }

  @Test
  public void getJobOfferById_WithValidInput_ShouldReturnTheJobOffer() throws Exception {
    UUID id = UUID.randomUUID();
    JobOffer jobOffer = new JobOffer();
    when(jobOfferService.getJobOfferById(id)).thenReturn(jobOffer);
    when(jobOfferAssembler.fromJobOffer(jobOffer)).thenReturn(new JobOfferResource());

    mockMvc.perform(get(JOB_OFFER_URL + id.toString())).andExpect(status().isOk());

    verify(jobOfferService, times(1)).getJobOfferById(id);
    verify(jobOfferAssembler, times(1)).fromJobOffer(jobOffer);
  }

  @Test
  public void getJobApplicationsByJobOfferId_WithValidInput_ShouldReturnListOfApplications() throws Exception {
    UUID id = UUID.randomUUID();
    int limit = 10;
    int offset = 0;
    JobApplicationStatus status = JobApplicationStatus.APPLIED;
    List<JobApplication> applications = new ArrayList<>();
    when(jobOfferService.getJobApplicationsByJobOfferId(id, status, limit, offset)).thenReturn(applications);
    when(jobApplicationAssembler.fromJobApplicationList(applications)).thenReturn(new ArrayList<>());

    mockMvc.perform(
        get(JOB_OFFER_URL + id.toString() + APPLICATIONS + "?applicationStatus=" + status.toString() + "&limit=" +
            limit + "&offset=" + offset))
        .andExpect(status().isOk());

    verify(jobOfferService, times(1)).getJobApplicationsByJobOfferId(id, status, limit, offset);
    verify(jobApplicationAssembler, times(1)).fromJobApplicationList(applications);
  }


}
