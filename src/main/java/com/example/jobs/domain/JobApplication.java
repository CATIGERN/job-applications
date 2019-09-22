package com.example.jobs.domain;

import com.example.jobs.domain.enums.JobApplicationStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@RequiredArgsConstructor
@ToString(exclude = {"jobOffer"})
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@Entity
@Table(name = "job_applications")
public class JobApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_applications_generator")
  @SequenceGenerator(name = "job_applications_generator", sequenceName = "job_applications_seq")
  @Column(name = "internal_id", updatable = false, nullable = false)
  private Long internalId;

  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "candidate_email", updatable = false, nullable = false)
  private String candidateEmail;

  @Column(name = "resume_text", updatable = false, nullable = false)
  private String resumeText;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_offers_internal_id", nullable = false)
  private JobOffer jobOffer;

  @Enumerated(EnumType.STRING)
  @Column(name = "application_status", nullable = false)
  private JobApplicationStatus applicationStatus;

  @Column(name = "created_time", updatable = false, nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date createdTime;

  @Column(name = "updated_time", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date updatedTime;

}
