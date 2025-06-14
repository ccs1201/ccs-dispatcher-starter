package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTemplateAutoConfig {

    private final Logger log = LoggerFactory.getLogger(RabbitTemplateAutoConfig.class);

    @PostConstruct
    public void init() {
        log.debug("Configurando RabbitTemplate");
    }

    @Bean
    protected RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory,
                                            final MessageConverter messageConverter,
                                            final MessageDispatcherProperties properties) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(properties.getExchangeName());
        template.setRoutingKey(properties.getRoutingKey());
        template.setMandatory(true);
        template.setReplyTimeout(properties.getReplyTimeOut());

        //todo
//        template.setReplyErrorHandler(); //estudar isto

        template.addBeforePublishPostProcessors(message -> {
            message.getMessageProperties().getHeaders().remove("__TypeId__");
            return message;
        });

        if (log.isDebugEnabled()) {
            template.setConfirmCallback((correlationData, ack, cause) -> {
                if (ack) {
                    log.debug("Mensagem confirmada pelo broker: {}", ack);
                } else {
                    log.debug("Mensagem não confirmada pelo broker: {}", cause);
                }
            });

            template.setReturnsCallback(returned -> log.debug("Mensagem retornada: {}", returned.getMessage() +
                    " code: " + returned.getReplyCode() +
                    " reason: " + returned.getReplyText()));
        }

        log.info("RabbitTemplate configurado com exchange: {}", properties.getExchangeName() +
                " e routing key: " + properties.getRoutingKey());

        return template;
    }
}
