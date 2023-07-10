package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ReqCommentDtoJsonTest {
    @Autowired
    private JacksonTester<ReqCommentDto> json;

    @Test
    void shouldReturnFullValidReqCommentDtoInJson() throws Exception {
        ReqCommentDto reqCommentDto = new ReqCommentDto();
        reqCommentDto.setText("Text");

        JsonContent<ReqCommentDto> result = json.write(reqCommentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Text");
    }
}