package br.com.messagedispatcher.config;

import br.com.messagedispatcher.TestApplication;
import br.com.messagedispatcher.integrator.MessageDispatcherIntegratorProvider;
import br.com.messagedispatcher.listener.RabbitMqMessageDispatcherListener;
import br.com.messagedispatcher.publisher.MessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TestApplication.class)
@TestPropertySource(properties = {
        "message.dispatcher.default-listener-enabled=false",
        "message.dispatcher.entity-events.enabled=false",
        "spring.application.name=message-dispatcher-test"
})
public class PublisherOnlyModeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessagePublisher messagePublisher;

    @Test
    public void testPublisherOnlyMode() {
        // Verifica se o MessagePublisher está disponível
        assertNotNull(messagePublisher, "MessagePublisher deve estar disponível mesmo no modo publisher-only");

        // Verifica se o RabbitMqMessageDispatcherListener NÃO está disponível
        assertThrows(NoSuchBeanDefinitionException.class, () ->
            applicationContext.getBean(RabbitMqMessageDispatcherListener.class)
        , "RabbitMqMessageDispatcherListener não deve estar disponível no modo publisher-only");

        // Verifica se o MessageDispatcherEntityEventsListener NÃO está disponível
        assertThrows(NoSuchBeanDefinitionException.class, () -> {
            applicationContext.getBean("messageDispatcherEntityListener");
        }, "MessageDispatcherEntityEventsListener não deve estar disponível quando entity-events.enabled=false");

        // Verifica se o MessageDispatcherIntegratorProvider NÃO está disponível
        assertThrows(NoSuchBeanDefinitionException.class, () ->
            applicationContext.getBean(MessageDispatcherIntegratorProvider.class)
        , "MessageDispatcherIntegratorProvider não deve estar disponível quando entity-events.enabled=false");
    }
}