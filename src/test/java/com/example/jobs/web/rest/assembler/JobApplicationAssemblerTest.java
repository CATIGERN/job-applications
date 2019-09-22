package com.example.jobs.web.rest.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.web.rest.resource.JobApplicationResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JobApplicationAssemblerTest {

  @InjectMocks
  private JobApplicationAssembler assembler;

  private JobApplication application;

  private JobApplicationResource resource;

  private List<JobApplication> applicationList;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    application = JobApplication.builder()
        .id(UUID.randomUUID())
        .applicationStatus(JobApplicationStatus.INVITED)
        .candidateEmail("abc@jobs.com")
        .resumeText("Dummy resume")
        .createdTime(new Date())
        .updatedTime(new Date())
        .build();

    resource = JobApplicationResource.builder()
        .id(UUID.randomUUID())
        .applicationStatus(JobApplicationStatus.INVITED)
        .candidateEmail("abc@jobs.com")
        .resumeText("Dummy resume")
        .build();

    applicationList = Arrays.asList(application);
  }

  @Test
  public void toJobApplication_WithValidInput_ShouldReturnCorrespondingJobApplication() {
    JobApplication application = assembler.toJobApplication(resource);
    assertEquals(resource.getId(), application.getId());
    assertEquals(resource.getApplicationStatus(), application.getApplicationStatus());
    assertEquals(resource.getCandidateEmail(), application.getCandidateEmail());
    assertNotNull(resource.getResumeText(), application.getResumeText());
  }

  @Test
  public void toJobApplication_WithNullInput_ShouldReturnNull() {
    JobApplication application = assembler.toJobApplication(null);
    assertNull(application);
  }

  @Test
  public void fromJobApplication_WithValidInput_ShouldReturnCorrespondingJobApplicationResource() {
    JobApplicationResource resource = assembler.fromJobApplication(application);
    assertEquals(application.getId(), resource.getId());
    assertEquals(application.getApplicationStatus(), resource.getApplicationStatus());
    assertEquals(application.getCandidateEmail(), resource.getCandidateEmail());
    assertEquals(application.getResumeText(), resource.getResumeText());
  }

  @Test
  public void fromJobApplication_WithNullInput_ShouldReturnNull() {
    JobApplicationResource resource = assembler.fromJobApplication(null);
    assertNull(resource);
  }

  @Test
  public void fromJobApplicationList_WithValidInput_ShouldReturnCorrespondingJobApplicationResourceList() {
    List<JobApplicationResource> resourceList = assembler.fromJobApplicationList(applicationList);
    JobApplicationResource resource = resourceList.get(0);
    JobApplication application = applicationList.get(0);
    assertEquals(application.getId(), resource.getId());
    assertEquals(application.getApplicationStatus(), resource.getApplicationStatus());
    assertEquals(application.getCandidateEmail(), resource.getCandidateEmail());
    assertEquals(application.getResumeText(), resource.getResumeText());
  }

  @Test
  public void fromJobApplicationList_WithNullInput_ShouldReturnNull() {
    List<JobApplicationResource> resourceList = assembler.fromJobApplicationList(null);
    assertNull(resourceList);
  }
}
