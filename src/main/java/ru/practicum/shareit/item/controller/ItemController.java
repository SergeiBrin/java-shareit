package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Поступил GET запрос в ItemController: метод getItem(), itemId={}", itemId);
        return service.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET запрос в ItemController: метод getItemsByUser(), userId={}", userId);
        return service.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Поступил GET запрос в ItemController: метод searchAvailableItems(), text={}", text);
        return service.searchAvailableItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил POST запрос в ItemController: метод createItem(), userId={}, ItemDto={} ", userId, itemDto);
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Поступил PATCH запрос в ItemController: метод updateItem(), userId={}, itemId={}, ItemDto={}",
                 userId, itemId, itemDto);
        return service.updateItem(userId, itemId, itemDto);
    }
}
