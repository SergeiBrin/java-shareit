package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest buildItemRequest(User user, ReqItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .creator(user)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static RespItemRequestDto buildItemRequestDto(ItemRequest itemRequest) {
        return RespItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.buildItemDtoList(itemRequest.getItems()))
                .build();
    }

    public static List<RespItemRequestDto> buildItemRequestDto(List<ItemRequest> itemRequest) {
        return itemRequest.stream()
                .map(ItemRequestMapper::buildItemRequestDto)
                .collect(Collectors.toList());
    }
}
