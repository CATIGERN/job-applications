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
import com.example.jobs.repository.JobApplicationRepository;
import com.example.jobs.service.impl.JobApplicationServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class JobApplicationServiceTest {

  @Mock
  private JobOfferService jobOfferService;

  @Mock
  private JobApplicationRepository repository;

  @Mock
  private NotificationPublisher notificationPublisher;

  @InjectMocks
  private JobApplicationService jobApplicationService = new JobApplicationServiceImpl();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private JobOffer jobOffer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    jobOffer = JobOffer.builder()
        .jobOfferStatus(JobOfferStatus.ACTIVE)
        .vacancies(1)
        .applications(new ArrayList<>())
        .build();
  }

  @Test
  public void createJobApplication_WithValidInput_ShouldCreateJobApplication() {
    JobApplication application = new JobApplication();
    UUID id = UUID.randomUUID();
    when(jobOfferService.getJobOfferById(id)).thenReturn(jobOffer);
    when(repository.save(application)).thenReturn(application);
    doNothing().when(notificationPublisher).publishEvent(any(PublishEvent.class));
    JobApplication jobApplication = jobApplicationService.createJobApplication(application, id);
    assertEquals(application, jobApplication);
    verify(jobOfferService, times(1)).getJobOfferById(id);
    verify(repository, times(1)).save(application);
    verify(notificationPublisher, times(1)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void createJobApplication_WithInactiveJobOffer_ShouldThrowBadRequestException() {
    JobApplication application = new JobApplication();
    UUID id = UUID.randomUUID();
    jobOffer.setJobOfferStatus(JobOfferStatus.INACTIVE);
    when(jobOfferService.getJobOfferById(id)).thenReturn(jobOffer);
    when(repository.save(application)).thenReturn(application);
    doNothing().when(notificationPublisher).publishEvent(any(PublishEvent.class));
    thrown.expect(ResponseStatusException.class);
    thrown.expectMessage("Job offer has no vacancies");
    JobApplication jobApplication = jobApplicationService.createJobApplication(application, id);
    assertEquals(application, jobApplication);
    verify(jobOfferService, times(1)).getJobOfferById(id);
    verify(repository, times(1)).save(application);
    verify(notificationPublisher, times(1)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void createJobApplication_WithSameEmailId_ShouldThrowBadRequestException() {
    JobApplication application = new JobApplication();
    application.setCandidateEmail("abc@jobs.com");
    UUID id = UUID.randomUUID();
    jobOffer.setApplications(Arrays.asList(JobApplication.builder().candidateEmail("abc@jobs.com").build()));
    when(jobOfferService.getJobOfferById(id)).thenReturn(jobOffer);
    when(repository.save(application)).thenReturn(application);
    doNothing().when(notificationPublisher).publishEvent(any(PublishEvent.class));
    thrown.expect(ResponseStatusException.class);
    thrown.expectMessage("Job Application for the given email already exists.");
    JobApplication jobApplication = jobApplicationService.createJobApplication(application, id);
    assertEquals(application, jobApplication);
    verify(jobOfferService, times(1)).getJobOfferById(id);
    verify(repository, times(1)).save(application);
    verify(notificationPublisher, times(1)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void getJobApplicationById_WithValidInput_ShouldReturnJobApplication() {
    UUID id = UUID.randomUUID();
    JobApplication application = new JobApplication();
    when(repository.findById(id)).thenReturn(application);
    JobApplication jobApplication = jobApplicationService.getJobApplicationById(id);
    assertEquals(application, jobApplication);
    verify(repository, times(1)).findById(id);
  }

  @Test
  public void getJobApplicationById_WithInvalidInput_ShouldThrowBadRequestException() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(null);
    thrown.expect(ResponseStatusException.class);
    thrown.expectMessage("No Job Application found associated with the ID.");
    JobApplication jobApplication = jobApplicationService.getJobApplicationById(id);
    verify(repository, times(1)).findById(id);
  }

  @Test
  public void updateJobApplication_WithValidInput_ShouldUpdateTheJobStatus() {
    UUID id = UUID.randomUUID();
    JobApplication application = getResourceForJobApplicationUpdate();
    when(repository.findById(id)).thenReturn(application);
    when(repository.save(application)).thenReturn(application);
    doNothing().when(notificationPublisher).publishEvent(any(PublishEvent.class));
    JobApplication jobApplication = jobApplicationService.updateJobApplication(id, JobApplicationStatus.INVITED);
    assertEquals(application, jobApplication);
    verify(repository, times(1)).findById(id);
    verify(repository, times(1)).save(application);
    verify(notificationPublisher, times(1)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void updateJobApplication_ForInactiveJobOffer_ShouldThrowBadRequestException() {
    UUID id = UUID.randomUUID();
    JobApplication application = getResourceForJobApplicationUpdate();
    application.getJobOffer().setJobOfferStatus(JobOfferStatus.INACTIVE);
    when(repository.findById(id)).thenReturn(application);
    thrown.expect(ResponseStatusException.class);
    thrown.expectMessage("Job offer is expired.");
    JobApplication jobApplication = jobApplicationService.updateJobApplication(id, JobApplicationStatus.INVITED);
    assertEquals(application, jobApplication);
    verify(repository, times(1)).findById(id);
    verify(repository, times(0)).save(application);
    verify(notificationPublisher, times(0)).publishEvent(any(PublishEvent.class));
  }

  @Test
  public void updateJobApplication_ForFinalJobApplication_ShouldMarkJobOfferInactive() {
    UUID id = UUID.randomUUID();
    JobApplication application = getResourceForJobApplicationUpdate();
    application.setApplicationStatus(JobApplicationStatus.HIRED);
    when(repository.findById(id)).thenReturn(application);
    when(repository.save(application)).thenReturn(application);
    doNothing().when(notificationPublisher).publishEvent(any(PublishEvent.class));
    doNothing().when(jobOfferService).markJobOfferInactive(application.getJobOffer());
    JobApplication jobApplication = jobApplicationService.updateJobApplication(id, JobApplicationStatus.HIRED);
    assertEquals(application, jobApplication);
    verify(repository, times(1)).findById(id);
    verify(repository, times(1)).save(application);
    verify(notificationPublisher, times(1)).publishEvent(any(PublishEvent.class));
    verify(jobOfferService, times(1)).markJobOfferInactive(application.getJobOffer());
  }

  private JobApplication getResourceForJobApplicationUpdate() {
    JobApplication application = new JobApplication();
    application.setApplicationStatus(JobApplicationStatus.APPLIED);
    jobOffer.setApplications(Arrays.asList(application));
    application.setJobOffer(jobOffer);
    return application;
  }
}
