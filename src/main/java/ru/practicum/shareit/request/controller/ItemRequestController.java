package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<RespItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET запрос в ItemRequestController: метод getUserRequests(), userId={}", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RespItemRequestDto getRequestByReqId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long requestId) {
        log.info("Поступил GET запрос в ItemRequestController: " +
                        "метод getRequestByReqId(), userId={}, requestId={} ", userId, requestId);
        return itemRequestService.getRequestByReqId(userId, requestId);
    }

    @GetMapping("/all")
    public List<RespItemRequestDto> getRequestsFromOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в ItemRequestController: " +
                        "метод getRequestsFromOthers(), userId={}, from={}, size={} ", userId, from, size);
        return itemRequestService.getRequestsFromOthers(userId, from, size);
    }

    @PostMapping
    public RespItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody ReqItemRequestDto itemRequestDto) {
        log.info("Поступил POST запрос в ItemRequestController: " +
                "метод createRequest(), userId={}, itemRequestDto={} ", userId, itemRequestDto);
        return itemRequestService.createRequest(userId, itemRequestDto);
    }
}
