package com.example.jobs.domain;

import com.example.jobs.domain.enums.JobOfferStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
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
@ToString(exclude = {"applications"})
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@Entity
@Table(name = "job_offers")
public class JobOffer {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_offers_generator")
  @SequenceGenerator(name = "job_offers_generator", sequenceName = "job_offers_seq")
  @Column(name = "internal_id", nullable = false)
  private Long internalId;

  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "job_title", nullable = false)
  private String jobTitle;

  @Column(name = "job_description", nullable = false)
  private String jobDescription;

  @Column(name = "location", nullable = false)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(name = "job_offer_status", nullable = false)
  private JobOfferStatus jobOfferStatus;

  @Column(name = "start_date", nullable = false)
  private String startDate;

  @Column(name = "vacancies", nullable = false)
  private int vacancies;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_offers_internal_id")
  private List<JobApplication> applications;

  @Column(name = "created_time", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date createdTime;

  @Column(name = "updated_time", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date updatedTime;

}
