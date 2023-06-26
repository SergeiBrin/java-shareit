package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingInfo;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class LongItemDtoJsonTest {
    @Autowired
    private JacksonTester<LongItemDto> json;

    @Test
    void shouldReturnFullValidLongItemDtoInJson() throws Exception {
        LongItemDto itemDto = new LongItemDto();
        itemDto.setId(1L);
        itemDto.setName("ItemDto name");
        itemDto.setDescription("ItemDto description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(new BookingInfo(1L, 2L));
        itemDto.setNextBooking(new BookingInfo(2L, 3L));
        itemDto.setComments(Collections.singletonList(
                new RespCommentDto(1L, "Text", "Name", LocalDateTime.now())));
        itemDto.setRequestId(1L);

        JsonContent<LongItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ItemDto name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ItemDto description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Text");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}