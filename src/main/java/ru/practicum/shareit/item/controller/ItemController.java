package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public List<LongItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET запрос в ItemController: метод getAllItemsByUser(), userId={}", userId);
        return itemService.getAllItemsByUser(userId);
    }

    @PostMapping("/{itemId}/comment")
    public RespCommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId,
                                        @Valid @RequestBody ReqCommentDto text) {
        log.info("Поступил POST запрос в ItemController: метод createComment(), userId={}, itemId={}, text={}",
                userId, itemId, text);

        return itemService.createComment(userId, itemId, text);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Поступил GET запрос в ItemController: метод searchAvailableItems(), text={}", text);
        return itemService.searchAvailableItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
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
