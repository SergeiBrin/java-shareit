package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class RespCommentDtoJsonTest {
    @Autowired
    private JacksonTester<RespCommentDto> json;

    @Test
    void shouldReturnFullValidRespCommentDtoInJson() throws Exception {
        RespCommentDto respCommentDto = new RespCommentDto();
        respCommentDto.setId(1L);
        respCommentDto.setText("Text");
        respCommentDto.setAuthorName("Author");
        respCommentDto.setCreated(LocalDateTime.of(2023, 6, 12, 12, 12 ,12));

        JsonContent<RespCommentDto> result = json.write(respCommentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Author");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-06-12T12:12:12");
    }
}