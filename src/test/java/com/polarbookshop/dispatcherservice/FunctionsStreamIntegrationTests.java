package com.polarbookshop.dispatcherservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class FunctionsStreamIntegrationTests {

  @Autowired
  private InputDestination inputDestination;
  @Autowired
  private OutputDestination outputDestination;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void whenOrderAcceptedThenDispatched() throws IOException {
    long orderId = 121;

    Message<OrderAcceptedMessage> orderAcceptedMessageMessage = MessageBuilder
        .withPayload(new OrderAcceptedMessage(orderId))
        .build();
    Message<OrderDispatchedMessage> orderDispatchedMessageMessage = MessageBuilder
        .withPayload(new OrderDispatchedMessage(orderId))
        .build();

    inputDestination.send(orderAcceptedMessageMessage);
    assertThat(objectMapper.readValue(outputDestination.receive().getPayload(), OrderDispatchedMessage.class))
        .isEqualTo(orderDispatchedMessageMessage.getPayload());
  }
}
