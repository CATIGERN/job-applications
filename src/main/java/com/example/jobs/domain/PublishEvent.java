package com.example.jobs.domain;

import com.example.jobs.domain.enums.EventType;
import com.example.jobs.domain.enums.PublishStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.UUID;

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
public class PublishEvent {

  private UUID id;

  private PublishStatus status;

  private EventType eventType;

  private String eventData;

  private Date txnTime;

}
