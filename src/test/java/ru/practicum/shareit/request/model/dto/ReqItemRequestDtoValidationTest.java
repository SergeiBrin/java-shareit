package ru.practicum.shareit.request.model.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReqItemRequestDtoValidationTest {
    private final ReqItemRequestDto requestDto = new ReqItemRequestDto("Text");
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testInvalidDescription() {
        requestDto.setDescription(null);

        Set<ConstraintViolation<ReqItemRequestDto>> violations = validator.validate(requestDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqItemRequestDto> violation = violations.iterator().next();
        assertThat("description", is(violation.getPropertyPath().toString()));
        assertThat("не должно равняться null", is(violation.getMessage()));
    }
}