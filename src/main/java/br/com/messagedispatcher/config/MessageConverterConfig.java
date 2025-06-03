package br.com.messagedispatcher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MessageConverterConfig {

    private final Logger log = LoggerFactory.getLogger(MessageConverterConfig.class);

    @Bean
    @Primary
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        if (log.isDebugEnabled()) {
            log.debug("Configurando Jackson2JsonMessageConverter");
        }
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
