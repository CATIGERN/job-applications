package com.example.jobs.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.domain.JobOffer;
import com.example.jobs.domain.PublishEvent;
import com.example.jobs.domain.enums.JobApplicationStatus;
import com.example.jobs.domain.enums.JobOfferStatus;
import com.example.jobs.notification.NotificationPublisher;
import com.example.jobs.repository.JobOfferRepository;
import com.example.jobs.service.impl.JobOfferServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobOfferServiceTest {

  @Mock
  private JobOfferRepository repository;

  @Mock
  private NotificationPublisher publisher;

  @InjectMocks
  private JobOfferService jobOfferService = new JobOfferServiceImpl();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getAllJobOffers_WithValidInput_ShouldReturnListOfJobOffers() {
    when(repository.findJobOffers("ACTIVE", 10, 0)).thenReturn(new ArrayList<>());
    List<JobOffer> result = jobOfferService.getAllJobOffers(JobOfferStatus.ACTIVE, 10, 0);
    assertEquals(0, result.size());
    verify(repository, times(1)).findJobOffers("ACTIVE", 10, 0);
  }

  @Test
  public void createJobOffer_WithValidInput_ShouldCreateJobOfferInDataBase() {
    JobOffer jobOffer = new JobOffer();
    jobOffer.setJobTitle("Job Title");
    when(repository.findByJobTitleIgnoreCase(jobOffer.getJobTitle())).thenReturn(null);
    when(repository.save(jobOffer)).thenReturn(jobOffer);
    doNothing().when(publisher).publishEvent(any(PublishEvent.class));
    JobOffer savedJobOffer = jobOfferService.createJobOffer(jobOffer);
    assertEquals(jobOffer, savedJobOffer);
    verify(repository, times(1)).findByJobTitleIgnoreCase(jobOffer.getJobTitle());
    verify(repository, times(1)).save(jobOffer);
    verify(publisher, times(1)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void createJobOffer_WithSameJobTitle_ShouldThrowBadRequestException() {
    JobOffer jobOffer = new JobOffer();
    jobOffer.setJobTitle("Job Title");
    when(repository.findByJobTitleIgnoreCase(jobOffer.getJobTitle())).thenReturn(new JobOffer());
    thrown.expect(ResponseStatusException.class);
    thrown.expectMessage("Job offer with job title: Job Title already exists.");
    JobOffer savedJobOffer = jobOfferService.createJobOffer(jobOffer);
    verify(repository, times(1)).findByJobTitleIgnoreCase(jobOffer.getJobTitle());
    verify(repository, times(0)).save(jobOffer);
    verify(publisher, times(0)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void getJobOfferById_WithValidId_ShouldReturnJobOffer() {
    JobOffer offer = new JobOffer();
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(offer);
    JobOffer jobOffer = jobOfferService.getJobOfferById(id);
    assertEquals(offer, jobOffer);
    verify(repository, times(1)).findById(id);
  }

  @Test
  public void getJobOfferById_WithInvalidId_ShouldThrowNotFoundException() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(null);
    thrown.expect(ResponseStatusException.class);
    thrown.expectMessage("No Job Offer found associated with the ID.");
    JobOffer jobOffer = jobOfferService.getJobOfferById(id);
    verify(repository, times(1)).findById(id);
  }

  @Test
  public void markJobOfferInactive_WithValidJobOffer_ShouldUpdateStatusToInactive() {
    JobOffer jobOffer = new JobOffer();
    when(repository.save(jobOffer)).thenReturn(jobOffer);
    doNothing().when(publisher).publishEvent(any(PublishEvent.class));
    jobOfferService.markJobOfferInactive(jobOffer);
    verify(repository, times(1)).save(jobOffer);
    verify(publisher, times(1)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void getJobApplicationsByJobOfferId_WithValidInput_ShouldReturnFilteredApplications() {
    JobOffer offer = new JobOffer();
    offer.setApplications(getListOfJobApplications());
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(offer);
    List<JobApplication> applicationList =
        jobOfferService.getJobApplicationsByJobOfferId(id, JobApplicationStatus.APPLIED, 10, 0);
    assertEquals(2, applicationList.size());
    verify(repository, times(1)).findById(id);
  }

  @Test
  public void getJobApplicationsByJobOfferId_WithValidInput_ShouldApplyLimitAndOffset() {
    JobOffer offer = new JobOffer();
    offer.setApplications(getListOfJobApplications());
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(offer);
    List<JobApplication> applicationList =
        jobOfferService.getJobApplicationsByJobOfferId(id, JobApplicationStatus.APPLIED, 1, 1);
    assertEquals(1, applicationList.size());
    verify(repository, times(1)).findById(id);
  }

  private List<JobApplication> getListOfJobApplications() {
    JobApplicationStatus[] statuses =
        new JobApplicationStatus[]{JobApplicationStatus.APPLIED, JobApplicationStatus.APPLIED,
            JobApplicationStatus.HIRED};
    List<JobApplication> applications = new ArrayList<>();
    for (JobApplicationStatus status : statuses) {
      JobApplication application = new JobApplication();
      application.setApplicationStatus(status);
      applications.add(application);
    }
    return applications;
  }
}
