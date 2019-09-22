package com.example.jobs.web.rest.resource;

import com.example.jobs.domain.enums.JobOfferStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobOfferResource {

  private UUID id;

  @NotBlank
  @Size(max = 50)
  private String jobTitle;

  @NotBlank
  @Size(max = 1000)
  private String jobDescription;

  @NotBlank
  @Size(max = 50)
  private String location;

  private JobOfferStatus jobOfferStatus;

  @NotNull
  @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}")
  @Size(max = 10)
  private String startDate;

  @NotNull
  @Max(value = 100)
  @Min(value = 1)
  private int vacancies;

  private int numberOfApplications;

}
