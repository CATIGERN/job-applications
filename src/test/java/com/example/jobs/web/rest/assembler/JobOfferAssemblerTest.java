package com.example.jobs.web.rest.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.web.rest.resource.JobOfferResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JobOfferAssemblerTest {

  @InjectMocks
  private JobOfferAssembler assembler;

  private JobOffer jobOffer;

  private JobOfferResource jobOfferResource;

  private List<JobOffer> jobOfferList;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    jobOffer = JobOffer.builder()
        .id(UUID.randomUUID())
        .internalId(new Long(1))
        .jobTitle("Dummy job")
        .jobDescription(" Job desc")
        .location("Hyd")
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .applications(new ArrayList<>())
        .startDate("2019-11-01")
        .vacancies(1)
        .createdTime(new Date())
        .updatedTime(new Date())
        .build();

    jobOfferList = Arrays.asList(jobOffer);

    jobOfferResource = JobOfferResource.builder()
        .id(UUID.randomUUID())
        .jobTitle("Dummy job")
        .jobDescription("Job desc")
        .location("Hyd")
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .startDate("2019-11-01")
        .vacancies(1)
        .build();
  }

  @Test
  public void fromJobOffer_WithValidInput_ShouldReturnCorrespondingJobOfferResource() {
    JobOfferResource resource = assembler.fromJobOffer(jobOffer);
    assertEquals(jobOffer.getId(), resource.getId());
    assertEquals(jobOffer.getJobTitle(), resource.getJobTitle());
    assertEquals(jobOffer.getJobDescription(), resource.getJobDescription());
    assertEquals(jobOffer.getLocation(), resource.getLocation());
    assertEquals(jobOffer.getJobOfferStatus(), resource.getJobOfferStatus());
    assertEquals(jobOffer.getStartDate(), resource.getStartDate());
    assertEquals(jobOffer.getVacancies(), resource.getVacancies());
  }

  @Test
  public void fromJobOffer_WithNullInput_ShouldReturnNull() {
    JobOfferResource resource = assembler.fromJobOffer(null);
    assertNull(resource);
  }

  @Test
  public void fromJobOffer_WithNullApplications_ShouldSetNumberOfApplicationsAsZero() {
    jobOffer.setApplications(null);
    JobOfferResource resource = assembler.fromJobOffer(jobOffer);
    assertEquals(0, resource.getNumberOfApplications());
  }

  @Test
  public void fromJobOfferList_WithValidInput_ShouldReturnCorrespondingListOfJobOfferResource() {
    List<JobOfferResource> resourceList = assembler.fromJobOfferList(jobOfferList);
    assertEquals(1, resourceList.size());
    JobOffer jobOffer = jobOfferList.get(0);
    JobOfferResource resource = resourceList.get(0);
    assertEquals(jobOffer.getId(), resource.getId());
    assertEquals(jobOffer.getJobTitle(), resource.getJobTitle());
    assertEquals(jobOffer.getJobDescription(), resource.getJobDescription());
    assertEquals(jobOffer.getLocation(), resource.getLocation());
    assertEquals(jobOffer.getJobOfferStatus(), resource.getJobOfferStatus());
    assertEquals(jobOffer.getStartDate(), resource.getStartDate());
    assertEquals(jobOffer.getVacancies(), resource.getVacancies());
  }

  @Test
  public void fromJobOfferList_WithNullInput_ShouldReturnNull() {
    List<JobOfferResource> resourceList = assembler.fromJobOfferList(null);
    assertNull(resourceList);
  }

  @Test
  public void toJobOffer_WithValidInput_ShouldReturnCorrespondingJobOffer() {
    JobOffer jobOffer = assembler.toJobOffer(jobOfferResource);
    assertEquals(jobOfferResource.getId(), jobOffer.getId());
    assertEquals(jobOfferResource.getJobTitle(), jobOffer.getJobTitle());
    assertEquals(jobOfferResource.getJobDescription(), jobOffer.getJobDescription());
    assertEquals(jobOfferResource.getJobOfferStatus(), jobOffer.getJobOfferStatus());
    assertEquals(jobOfferResource.getLocation(), jobOffer.getLocation());
    assertEquals(jobOfferResource.getVacancies(), jobOffer.getVacancies());
  }

  @Test(expected = ResponseStatusException.class)
  public void toJobOffer_WithInvalidDate_ShouldThrowException() {
    jobOfferResource.setStartDate("2020-20-34");
    JobOffer jobOffer = assembler.toJobOffer(jobOfferResource);
  }

  @Test
  public void toJobOffer_WithNullInput_ShouldReturnNull() {
    JobOffer jobOffer = assembler.toJobOffer(null);
    assertNull(jobOffer);
  }
}
