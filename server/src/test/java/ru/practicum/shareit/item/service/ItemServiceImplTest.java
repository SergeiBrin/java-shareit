package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

// Если внедрять зависимости непосредственно в не final поля класса, то можно создать конструктор без аргументов.
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private final Item item = new Item();
    private final ItemDto itemDto = new ItemDto();
    private final User user = new User();
    private final User author = new User();
    private final Booking lastBooking = new Booking();
    private final Booking nextBooking = new Booking();
    private final Comment comment = new Comment();
    private final ItemRequest request = new ItemRequest();


    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        author.setId(2L);
        author.setName("Author");
        author.setEmail("author@gmail.com");

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
        itemDto.setRequestId(null);

        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setItem(item);
        lastBooking.setBooker(new User());

        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setItem(item);
        nextBooking.setBooker(new User());

        comment.setId(1L);
        comment.setText("Text comment");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void getItemById_ShouldReturnValidFullLongItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByItemUserIdAndItemIdAndStatusAndStartBefore(
                        anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository
                .findByItemUserIdAndItemIdAndStatusAndStartAfter(
                        anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(nextBooking));

        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));

        LongItemDto dbItemDto = itemService.getItemById(1L, 1L);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
        assertThat(dbItemDto.getLastBooking(), is(notNullValue()));
        assertThat(dbItemDto.getNextBooking(), is(notNullValue()));
        assertThat(dbItemDto.getComments(), hasSize(1));
        assertThat(dbItemDto.getRequestId(), equalTo(1L));
    }

    @Test
    void getItemById_ShouldReturnValidLongItemDtoWithNoBookingsCommentsAndRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByItemUserIdAndItemIdAndStatusAndStartBefore(
                        anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository
                .findByItemUserIdAndItemIdAndStatusAndStartAfter(
                        anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(anyLong())).thenReturn(Collections.emptyList());

        item.setRequest(null);

        LongItemDto dbItemDto = itemService.getItemById(1L, 1L);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
        assertThat(dbItemDto.getLastBooking(), is(nullValue()));
        assertThat(dbItemDto.getNextBooking(), is(nullValue()));
        assertThat(dbItemDto.getComments(), is(empty()));
        assertThat(dbItemDto.getRequestId(), is(nullValue()));
    }

    @Test
    void getItemById_ShouldThrowNotFoundExceptionForItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L, 1L));

        assertThat("Вещи с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void getItemById_ShouldThrowNotFoundExceptionForUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L, 1L));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void getAllItemsByUser_ShouldReturnListOfLongItemDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository
                .findByItemUserIdAndStatusAndStartBefore(
                        anyLong(), any(Status.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository
                .findByItemUserIdAndStatusAndStartAfter(
                        anyLong(), any(Status.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(nextBooking));
        when(commentRepository.findByItemIdIn(anySet())).thenReturn(List.of(comment));

        List<LongItemDto> dbItemDtos = itemService.getAllItemsByUser(1L, 0, 10);
        assertThat(dbItemDtos, hasSize(1));

        LongItemDto dbItemDto = dbItemDtos.get(0);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
        assertThat(dbItemDto.getLastBooking(), is(notNullValue()));
        assertThat(dbItemDto.getNextBooking(), is(notNullValue()));
        assertThat(dbItemDto.getComments(), hasSize(1));
        assertThat(dbItemDto.getRequestId(), equalTo(1L));
    }

    @Test
    void getAllItemsByUser_ShouldReturnListOfLongItemDtosWithNoBookingsCommentsAndRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository
                .findByItemUserIdAndStatusAndStartBefore(
                        anyLong(), any(Status.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository
                .findByItemUserIdAndStatusAndStartAfter(
                        anyLong(), any(Status.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemIdIn(anySet())).thenReturn(Collections.emptyList());

        item.setRequest(null);

        List<LongItemDto> dbItemDtos = itemService.getAllItemsByUser(1L, 0, 10);
        assertThat(dbItemDtos, hasSize(1));

        LongItemDto dbItemDto = dbItemDtos.get(0);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
        assertThat(dbItemDto.getLastBooking(), is(nullValue()));
        assertThat(dbItemDto.getNextBooking(), is(nullValue()));
        assertThat(dbItemDto.getComments(), is(empty()));
        assertThat(dbItemDto.getRequestId(), is(nullValue()));
    }

    @Test
    void getAllItemsByUser_ShouldThrowNotFoundExceptionForUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.getAllItemsByUser(1L, 0, 10));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void searchAvailableItems_ShouldReturnNonEmptyItemDtoList() {
        when(itemRepository.findByText(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> dbItemDtos = itemService.searchAvailableItems("name", 0, 10);
        assertThat(dbItemDtos, hasSize(1));

        ItemDto dbItemDto = dbItemDtos.get(0);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
        assertThat(dbItemDto.getRequestId(), equalTo(1L));
    }

    @Test
    void searchAvailableItems_ShouldReturnEmptyItemDtoList() {
        List<ItemDto> dbItemDtos = itemService.searchAvailableItems("", 0, 10);
        assertThat(dbItemDtos, hasSize(0));
    }

    @Test
    void createComment_ShouldReturnValidRespCommentDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndEndBefore(
                        anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            Comment dbComment = invocationOnMock.getArgument(0);
            dbComment.setId(1L);
            return dbComment;
        });

        RespCommentDto dbComment = itemService.createComment(1L, 1L, new ReqCommentDto("Text comment"));

        assertThat(dbComment.getId(), equalTo(1L));
        assertThat(dbComment.getText(), equalTo("Text comment"));
        assertThat(dbComment.getCreated(), is(notNullValue()));
        assertThat(dbComment.getAuthorName(), equalTo("Author"));
    }

    @Test
    void createComment_ShouldAThrowIncorrectBookingException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndEndBefore(
                        anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        final IncorrectBookingException e = assertThrows(
                IncorrectBookingException.class,
                () -> itemService.createComment(1L, 1L, new ReqCommentDto("Text comment")));

        assertThat("User c id=1 ещё не брал Item c id=1 в аренду", equalTo(e.getMessage()));
    }

    @Test
    void createComment_ShouldAThrowNotFoundExceptionForUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(1L, 1L, new ReqCommentDto("Text comment")));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void createComment_ShouldAThrowNotFoundExceptionForItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(1L, 1L, new ReqCommentDto("Text comment")));

        assertThat("Вещи с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void createItem_ShouldReturnValidItemDtoWithNotNullRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item dbItem = invocationOnMock.getArgument(0);
            dbItem.setId(1L);
            return dbItem;
        });

        itemDto.setRequestId(1L);
        ItemDto createItemDto = itemService.createItem(1L, itemDto);

        assertThat(createItemDto.getId(), equalTo(1L));
        assertThat(createItemDto.getName(), equalTo("Item name"));
        assertThat(createItemDto.getDescription(), equalTo("Item description"));
        assertThat(createItemDto.getAvailable(), equalTo(true));
        assertThat(createItemDto.getRequestId(), equalTo(1L));
    }

    @Test
    void createItem_ShouldReturnValidItemDtoWithNullRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item dbItem = invocationOnMock.getArgument(0);
            dbItem.setId(1L);
            return dbItem;
        });

        itemDto.setRequestId(2L);
        ItemDto createItemDto = itemService.createItem(1L, itemDto);

        assertThat(createItemDto.getId(), equalTo(1L));
        assertThat(createItemDto.getName(), equalTo("Item name"));
        assertThat(createItemDto.getDescription(), equalTo("Item description"));
        assertThat(createItemDto.getAvailable(), equalTo(true));
        assertThat(createItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void createItem_ShouldThrowNotFoundExceptionForUser() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(1L, itemDto));

        assertThat(NotFoundException.class, equalTo(e.getClass()));
    }

    @Test
    void updateItem_ShouldReturnValidFullItemDto() {
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setName("Update item name");
        updateItemDto.setDescription("Update item description");
        updateItemDto.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto dbItemDto = itemService.updateItem(1L, 1L, updateItemDto);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Update item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Update item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(false));
    }

    @Test
    void updateItem_ShouldReturnValidItemDtoForName() {
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setName("Update item name");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto dbItemDto = itemService.updateItem(1L, 1L, updateItemDto);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Update item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
    }

    @Test
    void updateItem_ShouldReturnValidItemDtoForDescription() {
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setDescription("Update item description");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto dbItemDto = itemService.updateItem(1L, 1L, updateItemDto);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Update item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(true));
    }

    @Test
    void updateItem_ShouldReturnValidItemDtoForAvailable() {
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto dbItemDto = itemService.updateItem(1L, 1L, updateItemDto);

        assertThat(dbItemDto.getId(), equalTo(1L));
        assertThat(dbItemDto.getName(), equalTo("Item name"));
        assertThat(dbItemDto.getDescription(), equalTo("Item description"));
        assertThat(dbItemDto.getAvailable(), equalTo(false));
    }

    @Test
    void updateItem_ShouldThrowNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));

        assertThat("У пользователя нет вещи c id=1", equalTo(e.getMessage()));
    }
}