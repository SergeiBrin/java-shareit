package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void shouldReturnFullValidItemDtoInJson() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("ItemDto name");
        itemDto.setDescription("ItemDto description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ItemDto name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ItemDto description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}