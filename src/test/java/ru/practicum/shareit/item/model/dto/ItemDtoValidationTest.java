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

class ItemDtoValidationTest {
    private final ItemDto itemDto = new ItemDto();
    private Validator validator;

    @BeforeEach
    void setUp() {
        itemDto.setName("ItemDto name");
        itemDto.setDescription("ItemDto description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        Locale.setDefault(Locale.ENGLISH);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testInvalidName() {
        itemDto.setName("");

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertThat("name", is(violation.getPropertyPath().toString()));
        assertThat("must not be blank", is(violation.getMessage()));
    }

    @Test
    void testInvalidDescription() {
        itemDto.setDescription(null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertThat("description", is(violation.getPropertyPath().toString()));
        assertThat("must not be null", is(violation.getMessage()));
    }

    @Test
    void testInvalidAvailableForNull() {
        itemDto.setAvailable(null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertThat("available", is(violation.getPropertyPath().toString()));
        assertThat("must not be null", is(violation.getMessage()));
    }

    @Test
    void testInvalidAvailableForFalse() {
        itemDto.setAvailable(false);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertThat("available", is(violation.getPropertyPath().toString()));
        assertThat("must be true", is(violation.getMessage()));
    }
}