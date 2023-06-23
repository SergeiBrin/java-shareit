package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReqBookingDtoValidationTest {
    private final ReqBookingDto reqBookingDto = new ReqBookingDto();
    private Validator validator;

    @BeforeEach
    void setUp() {
        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.now().plusDays(1));
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testInvalidItemId() {
        reqBookingDto.setItemId(null);

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("itemId", is(violation.getPropertyPath().toString()));
        assertThat("не должно равняться null", is(violation.getMessage()));
    }

    @Test
    void testInvalidStartForNull() {
        reqBookingDto.setStart(null);

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("start", is(violation.getPropertyPath().toString()));
        assertThat("не должно равняться null", is(violation.getMessage()));
    }

    @Test
    void testInvalidStartForPast() {
        reqBookingDto.setStart(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("start", is(violation.getPropertyPath().toString()));
        assertThat("должно содержать сегодняшнее число или дату, которая еще не наступила",
                is(violation.getMessage()));
    }

    @Test
    void testInvalidEndForNull() {
        reqBookingDto.setEnd(null);

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("end", is(violation.getPropertyPath().toString()));
        assertThat("не должно равняться null", is(violation.getMessage()));
    }

    @Test
    void testInvalidEndForPastAndPresent() {
        reqBookingDto.setEnd(LocalDateTime.now());

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("end", is(violation.getPropertyPath().toString()));
        assertThat("должно содержать дату, которая еще не наступила", is(violation.getMessage()));
    }
}