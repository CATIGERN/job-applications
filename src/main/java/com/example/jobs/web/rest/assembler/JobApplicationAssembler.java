package com.example.jobs.web.rest.assembler;

import com.example.jobs.domain.JobApplication;
import com.example.jobs.web.rest.resource.JobApplicationResource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobApplicationAssembler {

  /**
   * Convert a job Application resource to Job Application domain object.
   *
   * @param resource The resource to be converted.
   * @return The job application domain object.
   */
  public JobApplication toJobApplication(JobApplicationResource resource) {
    if (resource == null) {
      return null;
    }
    JobApplication application = new JobApplication();
    BeanUtils.copyProperties(resource, application);
    return application;
  }

  /**
   * Convert a job application domain object to job application resource.
   *
   * @param application The job application domain object.
   * @return The converted Job application resource.
   */
  public JobApplicationResource fromJobApplication(JobApplication application) {
    if (application == null) {
      return null;
    }
    JobApplicationResource resource = new JobApplicationResource();
    BeanUtils.copyProperties(application, resource);
    return resource;
  }

  /**
   * Convert a job application domain list to job application resource list.
   *
   * @param applications The job application domain list to be converted.
   * @return The converted job application resource list.
   */
  public List<JobApplicationResource> fromJobApplicationList(List<JobApplication> applications) {
    if (applications == null) {
      return null;
    }
    return applications.stream().map(this::fromJobApplication).collect(Collectors.toList());
  }
}
