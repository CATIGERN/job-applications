package com.example.jobs.repository;

import com.example.jobs.domain.JobApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobApplicationRepository extends CrudRepository<JobApplication, Long> {

  /**
   * Find job application by its UUID.
   *
   * @param id The UUID of the job application.
   * @return The job application if exists, else null.
   */
  JobApplication findById(UUID id);
}
