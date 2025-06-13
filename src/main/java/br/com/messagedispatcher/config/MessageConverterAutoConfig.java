package br.com.messagedispatcher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConverterAutoConfig {

    private final Logger log = LoggerFactory.getLogger(MessageConverterAutoConfig.class);

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        if (log.isDebugEnabled()) {
            log.debug("Configurando Jackson2JsonMessageConverter");
        }
        var converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        var typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.addTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
}
