package br.com.ccs.dispatcher.config.objectMapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.logging.Logger;

@Configuration
public class ObjectMapperConfig {
    private final Logger log = Logger.getLogger(ObjectMapperConfig.class.getName());

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        log.info("Initializing Jackson ObjectMapper...");
        final ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true) // Habilita pretty-printing
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
        log.info("Jackson ObjectMapper initialized");

        return objectMapper;
    }
}
