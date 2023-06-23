package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private final User user = new User();
    private final ItemRequest itemRequest = new ItemRequest();
    private final ReqItemRequestDto requestDto = new ReqItemRequestDto();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User1");
        user.setEmail("user1@gmail.com");

        itemRequest.setId(1L);
        itemRequest.setCreator(user);
        itemRequest.setDescription("Text Request");
        itemRequest.setCreated(LocalDateTime.now());

        requestDto.setDescription("Text request");
    }

    @Test
    void getUserRequests_ShouldRThrowNotFoundException() {
        when(userService.getUserById(anyLong())).thenThrow(NotFoundException.class);

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getUserRequests(1L));

        assertThat(NotFoundException.class, equalTo(e.getClass()));
    }

    @Test
    void getUserRequests_ShouldReturnNonEmptyListOfRespItemRequestDtos() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findByCreatorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<RespItemRequestDto> requests = itemRequestService.getUserRequests(1L);
        assertThat(requests, hasSize(1));

        RespItemRequestDto respDto = requests.get(0);

        assertThat(respDto.getId(), equalTo(1L));
        assertThat(respDto.getDescription(), equalTo("Text Request"));
        assertThat(respDto.getCreated(), is(notNullValue()));
        assertThat(respDto.getItems(), is(empty()));
    }

    @Test
    void getUserRequests_ShouldReturnEmptyListOfRespItemRequestDtos() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findByCreatorIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<RespItemRequestDto> requests = itemRequestService.getUserRequests(1L);
        assertThat(requests, hasSize(0));
    }

    @Test
    void getRequestByReqId_ShouldReturnValidRespItemRequestDto() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        RespItemRequestDto respDto = itemRequestService.getRequestByReqId(1L, 1L);

        assertThat(respDto.getId(), equalTo(1L));
        assertThat(respDto.getDescription(), equalTo("Text Request"));
        assertThat(respDto.getCreated(), is(notNullValue()));
        assertThat(respDto.getItems(), is(empty()));
    }

    @Test
    void getRequestByReqId_ShouldThrowNotFoundExceptionForRequest() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestByReqId(1L, 1L));

        assertThat("Запроса с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void getRequestsFromOthers_ShouldReturnListOfRespItemRequestDtos() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findByCreatorIdNot(
                argThat(argument -> argument != 1L),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(itemRequest));

        List<RespItemRequestDto> respDtos = itemRequestService.getRequestsFromOthers(2L, 0, 10);
        assertThat(respDtos, hasSize(1));

        RespItemRequestDto respDto = respDtos.get(0);

        assertThat(respDto.getId(), equalTo(1L));
        assertThat(respDto.getDescription(), equalTo("Text Request"));
        assertThat(respDto.getCreated(), is(notNullValue()));
        assertThat(respDto.getItems(), is(empty()));
    }

    @Test
    void getRequestsFromOthers_ShouldReturnEmptyListOfRespItemRequestDtos() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findByCreatorIdNot(eq(1L),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<RespItemRequestDto> respDtos = itemRequestService.getRequestsFromOthers(1L, 0, 10);
        assertThat(respDtos, hasSize(0));
    }

    @Test
    void createRequest_ShouldReturnValidRespItemRequestDto() {
        when(itemRequestRepository.save(ArgumentMatchers.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> {
                            ItemRequest dbItemRequest = invocationOnMock.getArgument(0);
                            dbItemRequest.setId(1L);
                            return dbItemRequest;
                        });

        RespItemRequestDto respDto = itemRequestService.createRequest(1L, requestDto);

        assertThat(respDto.getId(), equalTo(1L));
        assertThat(respDto.getDescription(), equalTo("Text request"));
        assertThat(respDto.getCreated(), is(notNullValue()));
        assertThat(respDto.getItems(), is(empty()));
    }

    @Test
    void createRequest_ShouldThrowNotFoundException() {
        when(userService.getUserById(anyLong())).thenThrow(NotFoundException.class);

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createRequest(1L, requestDto));

        assertThat(NotFoundException.class, equalTo(e.getClass()));
    }
}