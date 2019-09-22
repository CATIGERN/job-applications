package com.example.jobs.web.rest.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.service.JobApplicationService;
import com.example.jobs.web.rest.assembler.JobApplicationAssembler;
import com.example.jobs.web.rest.resource.JobApplicationResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class JobApplicationControllerTest {

  @Mock
  private JobApplicationAssembler assembler;

  @Mock
  private JobApplicationService service;

  @InjectMocks
  private JobApplicationController controller;

  private MockMvc mockMvc;

  private JobApplicationResource resource;

  private static final String APPLICATION_JSON_UTF8_VALUE = "application/json; charset=utf-8";

  private ObjectMapper objectMapper = new ObjectMapper();

  private static final String APPLICATION_URL = "/jobmanagement/v1/application/";

  @Before
  public void setUp() {
    mockMvc = standaloneSetup(controller).build();

    resource = JobApplicationResource.builder()
        .id(UUID.randomUUID())
        .applicationStatus(JobApplicationStatus.INVITED)
        .candidateEmail("abc@jobs.com")
        .resumeText("Dummy resume")
        .build();
  }

  @Test
  public void createJobApplication_WithValidInput_ShouldCreateJobApplication() throws Exception {
    UUID id = UUID.randomUUID();
    JobApplication application = new JobApplication();
    when(service.createJobApplication(application, id)).thenReturn(application);
    when(assembler.toJobApplication(resource)).thenReturn(application);
    when(assembler.fromJobApplication(application)).thenReturn(resource);

    mockMvc.perform(post(APPLICATION_URL + id.toString()).contentType(APPLICATION_JSON_UTF8_VALUE)
        .content(objectMapper.writeValueAsString(resource))).andExpect(status().isCreated());

    verify(service, times(1)).createJobApplication(application, id);
    verify(assembler, times(1)).toJobApplication(resource);
    verify(assembler, times(1)).fromJobApplication(application);
  }

  @Test
  public void updateJobApplication_WithValidInput_ShouldUpdateTheApplication() throws Exception {
    UUID id = UUID.randomUUID();
    Map<String, String> map = new HashMap<>();
    map.put("status", "INVITED");
    JobApplication application = new JobApplication();
    when(service.updateJobApplication(id, JobApplicationStatus.INVITED)).thenReturn(application);
    when(assembler.fromJobApplication(application)).thenReturn(new JobApplicationResource());

    mockMvc.perform(patch(APPLICATION_URL + id.toString()).contentType(APPLICATION_JSON_UTF8_VALUE)
        .content(objectMapper.writeValueAsString(map))).andExpect(status().isOk());

    verify(service, times(1)).updateJobApplication(id, JobApplicationStatus.INVITED);
    verify(assembler, times(1)).fromJobApplication(application);
  }

  @Test
  public void updateJobApplication_WithInvalidInput_ShouldThrowBadRequestException() throws Exception {
    UUID id = UUID.randomUUID();
    Map<String, String> map = new HashMap<>();
    map.put("state", "INVITED");

    mockMvc.perform(patch(APPLICATION_URL + id.toString()).contentType(APPLICATION_JSON_UTF8_VALUE)
        .content(objectMapper.writeValueAsString(map))).andExpect(status().isBadRequest());
  }

  @Test
  public void getJobApplicationById_WithValidInput_ShouldReturnTheApplicationDetails() throws Exception {
    UUID id = UUID.randomUUID();
    JobApplication application = new JobApplication();
    when(service.getJobApplicationById(id)).thenReturn(application);
    when(assembler.fromJobApplication(application)).thenReturn(new JobApplicationResource());

    mockMvc.perform(get(APPLICATION_URL + id.toString())).andExpect(status().isOk());

    verify(service, times(1)).getJobApplicationById(id);
    verify(assembler, times(1)).fromJobApplication(application);
  }


}
