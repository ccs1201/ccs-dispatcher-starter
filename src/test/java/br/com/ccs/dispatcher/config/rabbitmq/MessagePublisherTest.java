package br.com.ccs.dispatcher.config.rabbitmq;

import br.com.ccs.dispatcher.config.DispatcherProperties;
import br.com.ccs.dispatcher.messaging.MessagePublisher;
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
    private DispatcherProperties properties;

    @Mock
    private RabbitTemplate rabbitTemplate;


}