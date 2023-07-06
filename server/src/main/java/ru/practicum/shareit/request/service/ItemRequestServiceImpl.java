package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.PageRequestFactory;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public List<RespItemRequestDto> getUserRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByCreatorIdOrderByCreatedDesc(userId);
        log.info("GET запрос в ItemRequestController обработан успешно. Метод getUserRequests(), userId={}", userId);

        return ItemRequestMapper.buildItemRequestDto(itemRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public RespItemRequestDto getRequestByReqId(Long userId, Long requestId) {
        userService.getUserById(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запроса с таким id=%d нет", requestId)));
        log.info("GET запрос в ItemRequestController обработан успешно. " +
                "Метод getRequestByReqId(), userId={}, requestId={}", userId, requestId);

        return ItemRequestMapper.buildItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RespItemRequestDto> getRequestsFromOthers(Long userId, int from, int size) {
        userService.getUserById(userId);

        Pageable page = PageRequestFactory
                .createPageRequest(from, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> itemRequests = itemRequestRepository.findByCreatorIdNot(userId, page);
        log.info("GET запрос в ItemRequestController обработан успешно. " +
                "Метод getRequestsFromOthers(), userId={}", userId);

        return ItemRequestMapper.buildItemRequestDto(itemRequests);
    }

    @Transactional
    @Override
    public RespItemRequestDto createRequest(Long userId, ReqItemRequestDto itemRequestDto) {
        User user = userService.getUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.buildItemRequest(user, itemRequestDto);
        ItemRequest createItemRequest = itemRequestRepository.save(itemRequest);
        log.info("POST запрос в ItemRequestController обработан успешно. " +
                "Метод createRequest(), userId={}, itemRequestDto={}, createItemRequest={}",
                userId, itemRequestDto, createItemRequest);

        return ItemRequestMapper.buildItemRequestDto(createItemRequest);
    }
}
