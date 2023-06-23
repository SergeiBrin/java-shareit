package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserValidationTest {
    private final User user = new User();
    private Validator validator;

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testInvalidEmail() {
        user.setEmail("user.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertThat("email", is(violation.getPropertyPath().toString()));
        assertThat("должно иметь формат адреса электронной почты", is(violation.getMessage()));
    }
}