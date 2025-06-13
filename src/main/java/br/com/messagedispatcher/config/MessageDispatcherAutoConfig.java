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

package br.com.messagedispatcher.config;

import br.com.messagedispatcher.config.rabbitmq.ExchangesQueuesBindingConfigAutoConfig;
import br.com.messagedispatcher.listener.RabbitMqMessageDispatcherListener;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto Configuração do {@link RabbitMqMessageDispatcherListener}
 * <p>
 * Auto Configuration of {@link RabbitMqMessageDispatcherListener}
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */
@Configuration
@AutoConfigureBefore({RabbitAutoConfiguration.class, ExchangesQueuesBindingConfigAutoConfig.class})
@ComponentScan(basePackages = "br.com.messagedispatcher")
public class MessageDispatcherAutoConfig {

}
