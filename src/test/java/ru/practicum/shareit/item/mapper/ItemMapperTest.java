package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemMapperTest {
    private final User user = new User();
    private final User booker = new User();
    private final ItemRequest request = new ItemRequest();
    private final Item item = new Item();
    private final ItemDto itemDto = new ItemDto();
    private final LongItemDto longitemDto = new LongItemDto();
    private final Booking lastBooking = new Booking();
    private final Booking nextBooking = new Booking();
    private final RespCommentDto commentDto = new RespCommentDto();
    private final Comment comment = new Comment();


    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@gmail.com");

        request.setId(1L);
        request.setCreator(new User());
        request.setDescription("Text description");
        request.setCreated(LocalDateTime.now());

        item.setId(1L);
        item.setUser(user);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequest(request);

        itemDto.setId(0L);
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);

        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);

        comment.setId(1L);
        comment.setText("Text comment");
        comment.setItem(item);
        comment.setAuthor(new User(3L, "author", "author@gmail.com"));
        comment.setCreated(LocalDateTime.now());

        commentDto.setId(1L);
        commentDto.setText("Text comment");
        commentDto.setAuthorName("author");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void buildItem() {
        Item buildItem = ItemMapper.buildItem(user, itemDto, request);

        assertThat(buildItem.getUser(), equalTo(user));
        assertThat(buildItem.getName(), equalTo(itemDto.getName()));
        assertThat(buildItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(buildItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(buildItem.getRequest(), equalTo(request));
    }

    @Test
    void buildItemDto_ShouldReturnItemDto() {
        ItemDto buildItemDto = ItemMapper.buildItemDto(item);

        assertThat(buildItemDto.getId(), equalTo(item.getId()));
        assertThat(buildItemDto.getName(), equalTo(item.getName()));
        assertThat(buildItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(buildItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(buildItemDto.getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void buildLongItemDto_ShouldReturnLongItemDto() {
        LongItemDto buildLongItemDto =
                ItemMapper.buildLongItemDto(item, List.of(lastBooking), List.of(nextBooking), List.of(commentDto));

        assertThat(buildLongItemDto.getId(),equalTo(item.getId()));
        assertThat(buildLongItemDto.getName(), equalTo(item.getName()));
        assertThat(buildLongItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(buildLongItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(buildLongItemDto.getLastBooking(), equalTo(new BookingInfo(lastBooking.getId(), lastBooking.getBooker().getId())));
        assertThat(buildLongItemDto.getNextBooking(), equalTo(new BookingInfo(nextBooking.getId(), nextBooking.getBooker().getId())));
        assertThat(buildLongItemDto.getComments(), equalTo(List.of(commentDto)));
        assertThat(buildLongItemDto.getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void buildItemDtoList_ShouldReturnItemDtoList() {
        List<ItemDto> buildItemDtos = ItemMapper.buildItemDtoList(List.of(item));
        assertThat(buildItemDtos, hasSize(1));

        ItemDto buildItemDto = buildItemDtos.get(0);

        assertThat(buildItemDto.getId(), equalTo(item.getId()));
        assertThat(buildItemDto.getName(), equalTo(item.getName()));
        assertThat(buildItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(buildItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(buildItemDto.getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void buildLongItemDtoList_ShouldReturnLongItemDtoList() {
        List<LongItemDto> buildLongItemDtos = ItemMapper.buildLongItemDtoList(
                List.of(item),
                Map.of(item, lastBooking),
                Map.of(item, nextBooking),
                Map.of(item, List.of(comment)));
        assertThat(buildLongItemDtos, hasSize(1));

        LongItemDto buildLongItemDto = buildLongItemDtos.get(0);

        assertThat(buildLongItemDto.getId(),equalTo(item.getId()));
        assertThat(buildLongItemDto.getName(), equalTo(item.getName()));
        assertThat(buildLongItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(buildLongItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(buildLongItemDto.getLastBooking(), equalTo(new BookingInfo(lastBooking.getId(), lastBooking.getBooker().getId())));
        assertThat(buildLongItemDto.getNextBooking(), equalTo(new BookingInfo(nextBooking.getId(), nextBooking.getBooker().getId())));
        assertThat(buildLongItemDto.getComments(), equalTo(List.of(commentDto)));
        assertThat(buildLongItemDto.getRequestId(), equalTo(item.getRequest().getId()));
    }
}