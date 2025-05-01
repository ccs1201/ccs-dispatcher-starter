
package br.com.ccs.dispatcher.config;

import br.com.ccs.dispatcher.CcsMessageDispatcher;
import br.com.ccs.dispatcher.config.properties.DispatcherConfigurationProperties;
import br.com.ccs.dispatcher.config.rabbitmq.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.logging.Logger;

@Configuration
@AutoConfigureBefore({RabbitAutoConfiguration.class, RabbitMQConfig.class})
@EnableConfigurationProperties(DispatcherConfigurationProperties.class)
@ComponentScan(basePackages = "br.com.ccs.dispatcher")
public class CcsDispatcherAutoConfiguration {

    private final Logger log = Logger.getLogger(CcsDispatcherAutoConfiguration.class.getName());


    @Bean
    @Primary
    public DispatcherConfigurationProperties ccsDispatcherProperties() {
        DispatcherConfigurationProperties properties = new DispatcherConfigurationProperties();
        log.info("Inicializando CcsDispatcherProperties");
        return properties;
    }

    @Bean
    public CcsMessageDispatcher ccsMessageDispatcher(
            ObjectMapper objectMapper,
            RequestMappingHandlerMapping handlerMapping,
            RequestMappingHandlerAdapter handlerAdapter,
            DispatcherConfigurationProperties properties,
            Environment environment) {

        // Se queueName não foi configurado, usa o nome da aplicação
        if (properties.getQueueName() == null) {
            properties.setQueueName(environment.getProperty("spring.application.name"));
        }
        return new CcsMessageDispatcher(
                objectMapper,
                handlerMapping,
                handlerAdapter);
    }
}
