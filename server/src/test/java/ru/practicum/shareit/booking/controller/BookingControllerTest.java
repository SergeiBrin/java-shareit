package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.dto.UserBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final RespBookingDto respBookingDto = new RespBookingDto();
    private final ReqBookingDto reqBookingDto = new ReqBookingDto();

    @BeforeEach
    void setUp() {
        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.now().plusDays(1));
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        respBookingDto.setId(1L);
        respBookingDto.setStart(LocalDateTime.now().plusDays(1));
        respBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        respBookingDto.setStatus(Status.WAITING);
        respBookingDto.setBooker(new UserBookingDto(1L));
        respBookingDto.setItem(new ItemBookingDto(1L, "Item name"));
    }

    @Test
    void getBookingById_ShouldReturnValidBookingDto() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(respBookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.booker.id", is(respBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(respBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(respBookingDto.getItem().getName())));
    }

    @Test
    void getBookingById_ShouldReturnNotFoundException() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserBookingsByState_ShouldReturnRespBookingDtoList() throws Exception {
        when(bookingService.getUserBookingsByState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(respBookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0]id", is(1L), Long.class))
                .andExpect(jsonPath("$.[0]start", is(notNullValue())))
                .andExpect(jsonPath("$.[0]end", is(notNullValue())))
                .andExpect(jsonPath("$.[0]status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.[0]booker.id", is(respBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0]item.id", is(respBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0]item.name", is(respBookingDto.getItem().getName())));
    }

    @Test
    void getUserBookingsByState_ShouldReturnNotFoundExceptionForUser() throws Exception {
        when(bookingService.getUserBookingsByState(not(eq(1L)), anyString(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnRespBookingDtoList() throws Exception {
        when(bookingService.getOwnerBookingsByState(anyLong(),anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(respBookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0]id", is(1L), Long.class))
                .andExpect(jsonPath("$.[0]start", is(notNullValue())))
                .andExpect(jsonPath("$.[0]end", is(notNullValue())))
                .andExpect(jsonPath("$.[0]status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.[0]booker.id", is(respBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0]item.id", is(respBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0]item.name", is(respBookingDto.getItem().getName())));
    }

//    @Test
//    void getOwnerBookingsByState_ShouldReturnUnsupportedStateException() throws Exception {
//        when(bookingService.getOwnerBookingsByState(anyLong(), anyString(), anyInt(), anyInt()))
//                .thenAnswer(invocation -> {
//                    String state = invocation.getArgument(1);
//                    if (!state.equals("ALL")
//                            && !state.equals("PAST")
//                            && !state.equals("CURRENT")
//                            && !state.equals("FUTURE")
//                            && !state.equals("WAITING")
//                            && !state.equals("REJECTED")) {
//                        throw new UnsupportedStateException("Error");
//                    }
//
//                    return new RespBookingDto();
//                });
//
//        mvc.perform(get("/bookings/owner")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("state", "UNSUPPORTED STATE")
//                        .param("from", "0")
//                        .param("size", "10")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void getOwnerBookingsByState_ShouldReturnNotFoundExceptionForUser() throws Exception {
        when(bookingService.getOwnerBookingsByState(not(eq(1L)), anyString(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_ShouldReturnValidRespBookingDto() throws Exception {
        when(bookingService.createBooking(anyLong(), any(ReqBookingDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ReqBookingDto reqBookingDto = invocationOnMock.getArgument(1);
                    respBookingDto.setId(1L);
                    respBookingDto.setStart(reqBookingDto.getStart());
                    respBookingDto.setEnd(reqBookingDto.getEnd());
                    respBookingDto.setStatus(Status.WAITING);
                    respBookingDto.setBooker(new UserBookingDto(1L));
                    respBookingDto.setItem(new ItemBookingDto(1L, "Item name"));
                    return respBookingDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.booker.id", is(respBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(respBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(respBookingDto.getItem().getName())));
    }

    @Test
    void createBooking_ShouldReturnNotFoundExceptionForUser() throws Exception {
        when(bookingService.createBooking(anyLong(), any(ReqBookingDto.class)))
                .thenAnswer(invocation -> {
                    ReqBookingDto reqBookingDto = invocation.getArgument(1);
                    if (reqBookingDto.getItemId() != 1L) {
                        throw new NotFoundException("Error");
                    }

                    return new RespBookingDto();
                });

        reqBookingDto.setItemId(2L);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBooking_ShouldReturnUpdatedRespBookingDto() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(respBookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.booker.id", is(respBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(respBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(respBookingDto.getItem().getName())));
    }

    @Test
    void updateBooking_ShouldReturnIncorrectBookingException() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(IncorrectBookingException.class);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_ShouldReturnNotFoundExceptionForUser() throws Exception {
        when(bookingService.updateBooking(not(eq(1L)), anyLong(), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBooking_ShouldReturnNotFoundExceptionForBooking() throws Exception {
        when(bookingService.updateBooking(anyLong(), not(eq(1L)), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}