package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.request.model.dto.RespItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final User user = new User(1L, "User", "user@gmail.com");
    private final ReqItemRequestDto reqItemRequestDto = new ReqItemRequestDto("Text request");
    private final RespItemRequestDto respItemRequestDto = new RespItemRequestDto(1L,
                                                                                "Text request",
                                                                                LocalDateTime.now(),
                                                                                Collections.emptyList());

    @Test
    void getUserRequests_ShouldReturnRespItemRequestDtoList() throws Exception {
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(respItemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0]id", is(respItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0]description", is(respItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0]created", is(notNullValue())))
                .andExpect(jsonPath("$.[0]items", hasSize(0)));
    }

    @Test
    void getUserRequests_ShouldReturnThrowNotFoundException() throws Exception {
        when(itemRequestService.getUserRequests(anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestByReqId_ShouldReturnRespItemRequestDto() throws Exception {
        when(itemRequestService.getRequestByReqId(anyLong(), anyLong())).thenReturn(respItemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("Text request")))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void getRequestByReqId_ShouldReturnThrowNotFoundExceptionForUser() throws Exception {
        when(itemRequestService.getRequestByReqId(not(eq(1L)), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestByReqId_ShouldReturnThrowNotFoundExceptionForRequest() throws Exception {
        when(itemRequestService.getRequestByReqId(anyLong(), not(eq(1L)))).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestsFromOthers_ShouldReturnRespItemRequestDtoList() throws Exception {
        when(itemRequestService.getRequestsFromOthers(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(respItemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0]id", is(respItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0]description", is(respItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0]created", is(notNullValue())))
                .andExpect(jsonPath("$.[0]items", hasSize(0)));
    }

    @Test
    void getRequestsFromOthers_ShouldReturnThrowNotFoundExceptionForUser() throws Exception {
        when(itemRequestService.getRequestsFromOthers(anyLong(), anyInt(), anyInt())).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRequest_ShouldReturnValidRespItemRequestDto() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any(ReqItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ReqItemRequestDto reqItemRequestDto = invocationOnMock.getArgument(1);
                    ItemRequest itemRequest = ItemRequestMapper.buildItemRequest(user, reqItemRequestDto);
                    RespItemRequestDto respItemRequestDto = ItemRequestMapper.buildItemRequestDto(itemRequest);
                    respItemRequestDto.setId(1L);
                    return respItemRequestDto;
                });

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("Text request")))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void createRequest_ShouldReturnThrowNotFoundException() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any(ReqItemRequestDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}