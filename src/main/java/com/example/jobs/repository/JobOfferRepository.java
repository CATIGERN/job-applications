package com.example.jobs.repository;

import com.example.jobs.domain.JobOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobOfferRepository extends CrudRepository<JobOffer, Long> {

  /**
   * Find the list of job offers by a given status, and limit the results with the specified offset.
   *
   * @param status The status of the job offers.
   * @param limit The number of results to be returned.
   * @param offset The number of results to be skipped.
   * @return The list of job offers.
   */
  @Query(value = "select * from job_offers where job_offer_status = ?1 limit ?2 offset ?3", nativeQuery = true)
  List<JobOffer> findJobOffers(String status, int limit, int offset);

  /**
   * Find job offer by its UUID.
   *
   * @param id The UUID of the job offer.
   * @return The job offer if exists, else null.
   */
  JobOffer findById(UUID id);

  /**
   * Find job offer by job Title.
   * @param jobTitle The title of the job.
   * @return The job offer if exists, else null.
   */
  JobOffer findByJobTitleIgnoreCase(String jobTitle);

}
