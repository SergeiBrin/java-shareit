package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.user.model.dto.UserBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class RespBookingDtoJsonTest {
    @Autowired
    private JacksonTester<RespBookingDto> json;

    @Test
    void shouldReturnFullValidReqBookingDtoInJson() throws Exception {
        RespBookingDto respBookingDto = new RespBookingDto();
        respBookingDto.setId(1L);
        respBookingDto.setStart(LocalDateTime.of(2023, 6, 23, 12, 12,12));
        respBookingDto.setEnd(LocalDateTime.of(2023, 8, 23, 12, 12,12));
        respBookingDto.setStatus(Status.WAITING);
        respBookingDto.setBooker(new UserBookingDto(1L));
        respBookingDto.setItem(new ItemBookingDto(1L, "Item name"));

        JsonContent<RespBookingDto> result = json.write(respBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-23T12:12:12");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-08-23T12:12:12");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item name");
    }
}