package br.com.ccs.dispatcher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MessageConverterConfig {

    private final Logger log = org.slf4j.LoggerFactory.getLogger(MessageConverterConfig.class);

    @Bean
    @Primary
    public org.springframework.amqp.support.converter.MessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        log.debug("Configurando Jackson2JsonMessageConverter");
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
