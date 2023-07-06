package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;

import java.util.List;

public interface ItemService {
    LongItemDto getItemById(Long userId, Long itemId);

    List<LongItemDto> getAllItemsByUser(Long userId, int from, int size);

    List<ItemDto> searchAvailableItems(String text, int from, int size);

    RespCommentDto createComment(Long userId, Long itemId, ReqCommentDto text);

    ItemDto  createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);
}
