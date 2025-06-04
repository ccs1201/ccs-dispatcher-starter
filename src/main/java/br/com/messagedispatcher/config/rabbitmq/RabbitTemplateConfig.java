package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class RabbitTemplateConfig {

    private final Logger log = LoggerFactory.getLogger(RabbitTemplateConfig.class);

    @Bean
    protected RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, final MessageConverter messageConverter, final MessageDispatcherProperties properties) {
        log.debug("Configurando RabbitTemplate");
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(properties.getExchangeName());
        template.setRoutingKey(properties.getRoutingKey());
        template.setMandatory(true);
        template.setReplyTimeout(properties.getReplyTimeOut());
        template.setTaskExecutor(Executors.newVirtualThreadPerTaskExecutor());
        template.addBeforePublishPostProcessors(message -> {
            message.getMessageProperties().getHeaders().remove("__TypeId__");
            return message;
        });

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("Mensagem entregue ao broker");
            } else {
                log.error("Mensagem não confirmada pelo broker: {}", cause);
            }
        });

        // Configurando retorno de mensagem
        template.setReturnsCallback(returned -> log.info("Mensagem retornada: {}", returned.getMessage() +
                " code: " + returned.getReplyCode() +
                " reason: " + returned.getReplyText()));

        log.info("RabbitTemplate configurado com exchange: {}", properties.getExchangeName() +
                " e routing key: " + properties.getRoutingKey());

        return template;
    }
}
