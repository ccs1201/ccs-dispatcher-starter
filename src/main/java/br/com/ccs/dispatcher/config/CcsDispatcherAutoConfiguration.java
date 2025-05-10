/*
 * Copyright 2024 Cleber Souza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Configuração do CcsDispatcherAutoConfiguration
 * <p>
 * Configuration of CcsDispatcherAutoConfiguration
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */


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
