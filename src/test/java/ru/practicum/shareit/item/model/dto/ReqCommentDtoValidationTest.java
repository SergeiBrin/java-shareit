package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class ReqCommentDtoValidationTest {
    private final ReqCommentDto reqCommentDto = new ReqCommentDto("Text comment");
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testInvalidText() {
        reqCommentDto.setText("");

        Set<ConstraintViolation<ReqCommentDto>> violations = validator.validate(reqCommentDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqCommentDto> violation = violations.iterator().next();
        assertThat("text", is(violation.getPropertyPath().toString()));
        assertThat("не должно быть пустым", is(violation.getMessage()));
    }
}