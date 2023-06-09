package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public LongItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        log.info("Поступил GET запрос в ItemController: метод getItem(), itemId={}", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<LongItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в ItemController: метод getAllItemsByUser(), userId={}", userId);
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public RespCommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId,
                                        @RequestBody ReqCommentDto text) {
        log.info("Поступил POST запрос в ItemController: метод createComment(), userId={}, itemId={}, text={}",
                userId, itemId, text);
        return itemService.createComment(userId, itemId, text);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в ItemController: " +
                "метод searchAvailableItems(), text={}", text);
        return itemService.searchAvailableItems(text, from, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Поступил POST запрос в ItemController: метод createItem(), userId={}, ItemDto={} ", userId, itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Поступил PATCH запрос в ItemController: метод updateItem(), userId={}, itemId={}, ItemDto={}",
                 userId, itemId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }
}
