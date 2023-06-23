package ru.practicum.shareit.request.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ReqItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ReqItemRequestDto> json;

    @Test
    void shouldReturnFullValidReqItemRequestDtoInJson() throws Exception {
        ReqItemRequestDto requestDto = new ReqItemRequestDto();
        requestDto.setDescription("Description");

        JsonContent<ReqItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
    }
}