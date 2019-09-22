package com.example.jobs.notification;

import com.example.jobs.domain.PublishEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPublisher.class);

  /**
   * Publisher to publish events related to any event.
   * @param event The event to be published.
   */
  public void publishEvent(PublishEvent event) {
    // In a better solution, we should push it to a Kafka Stream for listening
    // services to consume and take better action related to the events.
    LOGGER.info(event.toString());
  }
}
