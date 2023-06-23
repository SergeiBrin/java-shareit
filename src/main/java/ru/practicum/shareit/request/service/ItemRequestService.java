package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<RespItemRequestDto> getUserRequests(Long userId);

    RespItemRequestDto getRequestByReqId(Long userId, Long requestId);

    List<RespItemRequestDto> getRequestsFromOthers(Long userId, int from, int size);

    RespItemRequestDto createRequest(Long userId, ReqItemRequestDto itemRequestDto);
}
