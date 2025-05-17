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


package br.com.ccs.dispatcher.messaging.annotation;

import br.com.ccs.dispatcher.config.DispatcherAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
* Anotação para habilitar o uso do CcsDispatcher.
* Annotation to enable the use of CcsDispatcher.
 * <p>
 * Para habilitar o uso do CcsDispatcher, basta adicionar a anotação {@code @EnableMessageDispatcher} na classe de configuração do Spring.
 * To enable the use of CcsDispatcher, just add the {@code @EnableMessageDispatcher} annotation to the Spring configuration class.
 * <p>
 * Exemplo/Example:
 * <pre>
 * {@literal @}Configuration
 * {@literal @}EnableMessageDispatcher
 * public class AppConfig {
 *     // ...
 * }
 * </pre>
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DispatcherAutoConfig.class)
@SuppressWarnings("unused")
public @interface EnableMessageDispatcher {
}
