package br.com.ccs.dispatcher.util.validator;


import br.com.ccs.messagedispatcher.util.validator.BeanValidatorUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotEmpty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeanValidatorUtilTest {

    @Test
    public void validate() {
        var testBean = new testeBean();
        var ex = assertThrows(ConstraintViolationException.class, () -> BeanValidatorUtil.validate(testBean));
        assertEquals(ConstraintViolationException.class, ex.getClass());
    }
}

class testeBean {

    @NotEmpty
    private String teste;

}
