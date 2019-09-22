package com.example.jobs.web.rest.assembler;

import com.example.jobs.domain.JobOffer;
import com.example.jobs.web.rest.resource.JobOfferResource;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobOfferAssembler {

  /**
   * Convert a job offer domain object to job offer resource.
   *
   * @param jobOffer The job offer domain object.
   * @return The converted job offer resource.
   */
  public JobOfferResource fromJobOffer(JobOffer jobOffer) {
    if (jobOffer == null) {
      return null;
    }
    JobOfferResource resource = new JobOfferResource();
    BeanUtils.copyProperties(jobOffer, resource);
    resource.setNumberOfApplications(jobOffer.getApplications() == null ? 0 : jobOffer.getApplications().size());
    return resource;
  }

  /**
   * Convert a list of job offer domain object list to job offer resource list.
   *
   * @param jobOffers The job offer domain object list.
   * @return The converted job offer resource list.
   */
  public List<JobOfferResource> fromJobOfferList(List<JobOffer> jobOffers) {
    if (jobOffers == null) {
      return null;
    }
    return jobOffers.stream().map(this::fromJobOffer).collect(Collectors.toList());
  }

  /**
   * Convert a job offer resource to job offer domain object.
   *
   * @param resource The job offer resource to be converted.
   * @return The converted job offer domain object.
   */
  public JobOffer toJobOffer(JobOfferResource resource) {
    if (resource == null) {
      return null;
    }
    validateDate(resource.getStartDate());
    JobOffer jobOffer = new JobOffer();
    BeanUtils.copyProperties(resource, jobOffer);
    return jobOffer;
  }

  private void validateDate(String date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    simpleDateFormat.setLenient(false);
    try {
      Date convertedDate = simpleDateFormat.parse(date);
    } catch (ParseException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified start Date is not valid.");
    }
  }
}
