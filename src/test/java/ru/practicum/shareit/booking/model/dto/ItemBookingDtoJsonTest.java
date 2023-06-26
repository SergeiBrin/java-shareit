package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemBookingDtoJsonTest {
    @Autowired
    private JacksonTester<ItemBookingDto> json;

    @Test
    void shouldReturnFullValidItemBookingDtoInJson() throws Exception {
        ItemBookingDto itemBookingDto = new ItemBookingDto(1L, "Name");

        JsonContent<ItemBookingDto> result = json.write(itemBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Name");
    }
}