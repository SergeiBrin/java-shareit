package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReqCommentDtoValidationTest {
    private final ReqCommentDto reqCommentDto = new ReqCommentDto();
    private Validator validator;

    @BeforeEach
    void setUp() {
        reqCommentDto.setText("Text");

        Locale.setDefault(Locale.ENGLISH);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testInvalidText() {
        reqCommentDto.setText("");

        Set<ConstraintViolation<ReqCommentDto>> violations = validator.validate(reqCommentDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqCommentDto> violation = violations.iterator().next();
        assertThat("text", is(violation.getPropertyPath().toString()));
        assertThat("must not be blank", is(violation.getMessage()));
    }
}