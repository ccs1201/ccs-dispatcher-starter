package br.com.ccs.dispatcher.config.rabbitmq;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.ccs.messagedispatcher.messaging.publisher.MessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
public class MessagePublisherTest {

    @InjectMocks
    private MessagePublisher messagePublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageDispatcherProperties properties;

    @Mock
    private RabbitTemplate rabbitTemplate;


}