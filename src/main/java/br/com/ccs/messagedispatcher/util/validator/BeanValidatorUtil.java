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

package br.com.ccs.messagedispatcher.util.validator;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Classe utilitária para validação de beans.
 * <p>
 * Esta classe utiliza a API de validação do Java Bean Validation (JSR-380) para validar objetos.
 * Ela é uma classe utilitária e não deve ser instanciada diretamente.
 * <p>
 * Utility class for bean validation.
 * <p>
 * This class uses the Java Bean Validation API (JSR-380) to validate objects.
 * It is a utility class and should not be instantiated directly.
 * <p>
 * Exemplo / Example:
 * <pre>
 *     BeanValidatorUtil.validate(bean);
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 10/05/2025
 */
public class BeanValidatorUtil {

    private BeanValidatorUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final Validator validator;

    static {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    /**
     * Valida um objeto.
     * <p>
     * Este método valida um objeto e lança uma exceção {@link ConstraintViolationException} se o objeto estiver em um estado inválido.
     * <p>
     * This method validates an object and throws a {@link ConstraintViolationException} if the object is in an invalid state.
     * <p>
     * Exemplo / Example:
     * <pre>
     *     BeanValidatorUtil.validate(bean);
     *
     * @param object o objeto a ser validado.
     * @param <T>    o tipo do objeto a ser validado.
     * @throws ConstraintViolationException se o objeto for inválido.
     */
    public static <T> void validate(T object) {
        var violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
