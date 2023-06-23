package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemRequestMapperTest {
    private final User user = new User();
    private final ItemRequest itemRequest = new ItemRequest();
    private final ReqItemRequestDto requestDto = new ReqItemRequestDto();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        itemRequest.setId(1L);
        itemRequest.setCreator(new User());
        itemRequest.setDescription("Text description");
        itemRequest.setCreated(LocalDateTime.now());

        requestDto.setDescription("Text request");
    }

    @Test
    void buildItemRequest_ShouldReturnItemRequest() {
        ItemRequest buildItemRequest = ItemRequestMapper.buildItemRequest(user, requestDto);

        assertThat(buildItemRequest.getId(), equalTo(null));
        assertThat(buildItemRequest.getCreator(), equalTo(user));
        assertThat(buildItemRequest.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(buildItemRequest.getCreated(), is(notNullValue()));
        assertThat(buildItemRequest.getItems(), is(empty()));
    }

    @Test
    void buildItemRequestDto_ShouldReturnRespItemRequestDto() {
        RespItemRequestDto buildRespItemRequestDto = ItemRequestMapper.buildItemRequestDto(itemRequest);

        assertThat(buildRespItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(buildRespItemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(buildRespItemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(buildRespItemRequestDto.getItems(), equalTo(itemRequest.getItems()));
    }

    @Test
    void buildItemRequestDto_ShouldReturnRespItemRequestDtoList() {
        List<RespItemRequestDto> buildRespItemRequestDtos = ItemRequestMapper.buildItemRequestDto(List.of(itemRequest));
        assertThat(buildRespItemRequestDtos, hasSize(1));

        RespItemRequestDto buildRespItemRequestDto = buildRespItemRequestDtos.get(0);

        assertThat(buildRespItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(buildRespItemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(buildRespItemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(buildRespItemRequestDto.getItems(), equalTo(itemRequest.getItems()));
    }
}