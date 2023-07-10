package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.service.ItemClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Поступил GET запрос в ItemController: метод getItem(), itemId={}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                    @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в ItemController: метод getAllItemsByUser(), userId={}", userId);
        return itemClient.getAllItemsByUser(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody ReqCommentDto text) {
        log.info("Поступил POST запрос в ItemController: метод createComment(), userId={}, itemId={}, text={}",
                userId, itemId, text);
        return itemClient.createComment(userId, itemId, text);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItems(@RequestParam String text,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                       @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в ItemController: " +
                "метод searchAvailableItems(), text={}", text);
        return itemClient.searchAvailableItems(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил POST запрос в ItemController: метод createItem(), userId={}, ItemDto={} ", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Поступил PATCH запрос в ItemController: метод updateItem(), userId={}, itemId={}, ItemDto={}",
                 userId, itemId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}
