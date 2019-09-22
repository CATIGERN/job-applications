package com.example.jobs.web.rest.resource;

import com.example.jobs.domain.enums.JobApplicationStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class JobApplicationResource {

  private UUID id;

  @Email
  @Size(max = 50)
  @NotNull
  private String candidateEmail;

  @NotBlank
  @Size(max = 1000)
  private String resumeText;

  private JobApplicationStatus applicationStatus;

}
