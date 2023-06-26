package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserJsonTest {
    @Autowired
    private JacksonTester<User> json;

    @Test
    void shouldReturnFullValidUserInJson() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        JsonContent<User> result = json.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@gmail.com");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }
}