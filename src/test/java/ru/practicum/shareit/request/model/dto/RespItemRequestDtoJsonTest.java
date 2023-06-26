package ru.practicum.shareit.request.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RespItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<RespItemRequestDto> json;

    @Test
    void shouldReturnFullValidRespItemRequestDtoInJson() throws Exception {
        RespItemRequestDto requestDto = new RespItemRequestDto();
        ItemDto itemDto = new ItemDto(1L, "Item name", "Item description", true, 1L);
        requestDto.setId(1L);
        requestDto.setDescription("Description");
        requestDto.setCreated(LocalDateTime.of(2023, 10, 23, 10, 10, 10));
        requestDto.setItems(List.of(itemDto));

        JsonContent<RespItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-23T10:10:10");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
    }
}