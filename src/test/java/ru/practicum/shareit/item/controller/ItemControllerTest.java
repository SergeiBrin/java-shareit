package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IncorrectBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

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

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final User user = new User();
    private final ItemDto itemDto = new ItemDto();
    private final LongItemDto longItemDto = new LongItemDto();
    private final ReqCommentDto reqCommentDto = new ReqCommentDto();
    private final RespCommentDto respCommentDto = new RespCommentDto();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        itemDto.setId(0L);
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        longItemDto.setId(1L);
        longItemDto.setName("Item name");
        longItemDto.setDescription("Item description");
        longItemDto.setAvailable(true);

        reqCommentDto.setText("Text comment");
    }

    @Test
    void getItemById_ShouldReturnValidLongItemDto() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(longItemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(longItemDto.getName())))
                .andExpect(jsonPath("$.description", is(longItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(longItemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(longItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(longItemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(longItemDto.getComments())))
                .andExpect(jsonPath("$.requestId", is(longItemDto.getRequestId())));
    }

    @Test
    void getItemById_ShouldReturnNotFoundExceptionForUser() throws Exception {
        when(itemService.getItemById(not(eq(1L)), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemById_ShouldReturnNotFoundExceptionForItem() throws Exception {
        when(itemService.getItemById(anyLong(), not(eq(1L)))).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByUser_ShouldReturnLongItemDtoList() throws Exception {
        when(itemService.getAllItemsByUser(anyLong(), anyInt(), anyInt())).thenReturn(List.of(longItemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0]id", is(1L), Long.class))
                .andExpect(jsonPath("$.[0]name", is(longItemDto.getName())))
                .andExpect(jsonPath("$.[0]description", is(longItemDto.getDescription())))
                .andExpect(jsonPath("$.[0]available", is(longItemDto.getAvailable())))
                .andExpect(jsonPath("$.[0]lastBooking", is(longItemDto.getLastBooking())))
                .andExpect(jsonPath("$.[0]nextBooking", is(longItemDto.getNextBooking())))
                .andExpect(jsonPath("$.[0]comments", is(longItemDto.getComments())))
                .andExpect(jsonPath("$.[0]requestId", is(longItemDto.getRequestId())));
    }

    @Test
    void getAllItemsByUser_ShouldReturnThrowNotFoundExceptionForUser() throws Exception {
        when(itemService.getAllItemsByUser(anyLong(), anyInt(), anyInt())).thenThrow(NotFoundException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_ShouldReturnValidRespCommentDto() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(ReqCommentDto.class)))
                .thenAnswer(invocationOnMock -> {
            ReqCommentDto reqCommentDto = invocationOnMock.getArgument(2);
            respCommentDto.setId(1L);
            respCommentDto.setText(reqCommentDto.getText());
            respCommentDto.setAuthorName("Author");
            respCommentDto.setCreated(LocalDateTime.now());
            return respCommentDto;
        });

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(respCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(respCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(respCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void createComment_ShouldReturnIncorrectBookingException() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(ReqCommentDto.class)))
                .thenThrow(IncorrectBookingException.class);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_ShouldReturnINotFoundExceptionForUser() throws Exception {
        when(itemService.createComment(not(eq(1L)), anyLong(), any(ReqCommentDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(reqCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_ShouldReturnINotFoundExceptionForItem() throws Exception {
        when(itemService.createComment(anyLong(), not(eq(1L)), any(ReqCommentDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(reqCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchAvailableItems_ShouldReturn() throws Exception {
        when(itemService.searchAvailableItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        itemDto.setId(1L);
        mvc.perform(get("/items/search")
                        .param("text", "item")
                        .param("from", "1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0]id", is(1L), Long.class))
                .andExpect(jsonPath("$.[0]name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0]description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0]available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0]requestId", is(itemDto.getRequestId())));
    }

    @Test
    void createItem_ShouldReturnValidItemDto() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto itemDto = invocationOnMock.getArgument(1);
            Item item = ItemMapper.buildItem(user, itemDto, null);
            item.setId(1L);
            return ItemMapper.buildItemDto(item);
        });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void createItem_ShouldReturnNotFoundExceptionForUser() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenThrow(NotFoundException.class);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_ShouldReturnFullUpdateOfItemDto() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto itemDto = invocationOnMock.getArgument(2);
            Item item = ItemMapper.buildItem(user, itemDto, null);
            item.setId(1L);
            return ItemMapper.buildItemDto(item);
        });

        ItemDto updateItemDto = new ItemDto(
                0L, "Update name", "Update description", true, null);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(updateItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updateItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(updateItemDto.getRequestId())));
    }

    @Test
    void updateItem_ShouldReturnUpdateItemDtoForName() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto updateItemDto = invocationOnMock.getArgument(2);
            itemDto.setId(1L);
            itemDto.setName(updateItemDto.getName());
            return itemDto;
        });

        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setName("Update name");

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void updateItem_ShouldReturnUpdateItemDtoForDescription() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto updateItemDto = invocationOnMock.getArgument(2);
            itemDto.setId(1L);
            itemDto.setDescription(updateItemDto.getDescription());
            return itemDto;
        });

        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setDescription("Update description");

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void updateItem_ShouldReturnUpdateItemDtoForAvailable() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto updateItemDto = invocationOnMock.getArgument(2);
            itemDto.setId(1L);
            itemDto.setAvailable(updateItemDto.getAvailable());
            return itemDto;
        });

        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setAvailable(false);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void updateItem_ShouldReturnUpdateItemDtoForRequestId() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto updateItemDto = invocationOnMock.getArgument(2);
            itemDto.setId(1L);
            itemDto.setRequestId(updateItemDto.getRequestId());
            return itemDto;
        });

        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setRequestId(1L);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }
}