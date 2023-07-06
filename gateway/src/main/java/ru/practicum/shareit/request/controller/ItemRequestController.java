package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET запрос в ItemRequestController: метод getUserRequests(), userId={}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByReqId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long requestId) {
        log.info("Поступил GET запрос в ItemRequestController: " +
                        "метод getRequestByReqId(), userId={}, requestId={} ", userId, requestId);
        return itemRequestClient.getRequestByReqId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsFromOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в ItemRequestController: " +
                        "метод getRequestsFromOthers(), userId={}", userId);
        return itemRequestClient.getRequestsFromOthers(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ReqItemRequestDto itemRequestDto) {
        log.info("Поступил POST запрос в ItemRequestController: " +
                "метод createRequest(), userId={}, itemRequestDto={} ", userId, itemRequestDto);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }
}
