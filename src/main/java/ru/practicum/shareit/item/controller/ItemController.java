package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
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
    private final UserValidator userValidator;
    private final ItemValidator itemValidator;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Поступил GET запрос в ItemController: метод getItem(), itemId={}", itemId);
        itemValidator.checkIfItemExistsById(itemId);

        return service.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET запрос в ItemController: метод getItemsByUser(), userId={}", userId);
        userValidator.checkIfUserExistsById(userId);

        return service.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Поступил GET запрос в ItemController: метод searchAvailableItems(), text={}", text);

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return service.searchAvailableItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил POST запрос в ItemController: метод createItem(), userId={}, ItemDto={} ", userId, itemDto);
        userValidator.checkIfUserExistsById(userId);

        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Поступил PATCH запрос в ItemController: метод updateItem(), userId={}, itemId={}, ItemDto={}",
                 userId, itemId, itemDto);
        userValidator.checkIfUserExistsById(userId);
        itemValidator.checkIfItemExistsById(itemId);
        userValidator.checkConnectionOfUserWithItem(userId, itemId);

        return service.updateItem(userId, itemId, itemDto);
    }
}
