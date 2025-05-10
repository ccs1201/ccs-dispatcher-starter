package br.com.ccs.dispatcher.config.objectMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.DateFormat;
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
                .registerModule(new JavaTimeModule())
                .setDateFormat(DateFormat.getDateTimeInstance());
        log.info("Jackson ObjectMapper initialized");

        return objectMapper;
    }
}
