package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Locale;
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

        Locale.setDefault(Locale.ENGLISH);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testInvalidItemId() {
        reqBookingDto.setItemId(null);

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("itemId", is(violation.getPropertyPath().toString()));
        assertThat("must not be null", is(violation.getMessage()));
    }

    @Test
    void testInvalidStartForNull() {
        reqBookingDto.setStart(null);

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("start", is(violation.getPropertyPath().toString()));
        assertThat("must not be null", is(violation.getMessage()));
    }

    @Test
    void testInvalidStartForPast() {
        reqBookingDto.setStart(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("start", is(violation.getPropertyPath().toString()));
        assertThat("must be a date in the present or in the future",
                is(violation.getMessage()));
    }

    @Test
    void testInvalidEndForNull() {
        reqBookingDto.setEnd(null);

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("end", is(violation.getPropertyPath().toString()));
        assertThat("must not be null", is(violation.getMessage()));
    }

    @Test
    void testInvalidEndForPastAndPresent() {
        reqBookingDto.setEnd(LocalDateTime.now());

        Set<ConstraintViolation<ReqBookingDto>> violations = validator.validate(reqBookingDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ReqBookingDto> violation = violations.iterator().next();
        assertThat("end", is(violation.getPropertyPath().toString()));
        assertThat("must be a future date", is(violation.getMessage()));
    }
}