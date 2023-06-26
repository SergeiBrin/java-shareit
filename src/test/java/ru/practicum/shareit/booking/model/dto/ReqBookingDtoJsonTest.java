package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ReqBookingDtoJsonTest {
    @Autowired
    private JacksonTester<ReqBookingDto> json;

    @Test
    void shouldReturnFullValidReqBookingDtoInJson() throws Exception {
        ReqBookingDto reqBookingDto = new ReqBookingDto();
        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.of(2023, 6, 23, 12, 12,12));
        reqBookingDto.setEnd(LocalDateTime.of(2023, 8, 23, 12, 12,12));

        JsonContent<ReqBookingDto> result = json.write(reqBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-23T12:12:12");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-08-23T12:12:12");
    }

}