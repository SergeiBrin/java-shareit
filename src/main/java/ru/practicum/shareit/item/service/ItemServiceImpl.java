package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public LongItemDto getItemById(Long userId, Long itemId) {
        checkIfUserExistsById(userId);
        Item dbItem = checkIfItemExistsById(itemId);

        PageRequest pageForLastBooking = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"));
        PageRequest pageForNextBooking = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "start"));

        List<Booking> lastBooking = bookingRepository.findByItemUserIdAndItemIdAndStatusAndStartBefore(
                userId,
                itemId,
                Status.APPROVED,
                LocalDateTime.now(),
                pageForLastBooking);

        List<Booking> nextBooking = bookingRepository.findByItemUserIdAndItemIdAndStatusAndStartAfter(
                userId,
                itemId,
                Status.APPROVED,
                LocalDateTime.now(),
                pageForNextBooking);

        List<Comment> dbComments = commentRepository.findByItemId(itemId);
        List<RespCommentDto> commentDtos = CommentMapper.buildCommentDtoList(dbComments);
        log.info("GET запрос в ItemController обработан успешно. Метод getItemById(), itemId={}, Item={}", itemId, dbItem);

        return ItemMapper.buildLongItemDto(dbItem, lastBooking, nextBooking, commentDtos);
    }

    @Override
    public List<LongItemDto> getAllItemsByUser(Long userId) {
        checkIfUserExistsById(userId);

        Map<Long, Item> dbItems = itemRepository.findByUserId(userId, Sort.by("id"))
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        // Возвращаю все APPROVED last и next bookings пользователя, чтобы избежать N+1
        // Упаковываю их в Map с поведением first-win, чтобы убрать лишние Item, которые
        // могут быть забронированы еще раньше или ещё позже.
        // Map для того, чтобы сделать contains(Item) вместо for each. Так быстрее.
        Map<Item, Booking> last = bookingRepository
                .findByItemUserIdAndStatusAndStartBefore(
                        userId,
                        Status.APPROVED,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (existing, replacement) -> existing));

        Map<Item, Booking> next = bookingRepository
                .findByItemUserIdAndStatusAndStartAfter(
                        userId,
                        Status.APPROVED,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (existing, replacement) -> existing));

        Map<Item, List<Comment>> dbComments = commentRepository.findByItemIdIn(dbItems.keySet())
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        log.info("GET запрос в ItemController обработан успешно. Метод getItemsByUser(), userId={}, itemsByUser={}",
                userId, dbItems);

        return ItemMapper.buildLongItemDtoList(new ArrayList<>(dbItems.values()), last, next, dbComments);
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        if (text.isEmpty()) {
            log.info("GET запрос в ItemController не обработан, так как text пуст. Метод searchAvailableItems(), text={}", text);
            return new ArrayList<>();
        }

        List<Item> searchItems = itemRepository.findByText(text);
        log.info("GET запрос в ItemController обработан успешно. Метод searchAvailableItems(), text={}, searchItems={}", text, searchItems);

        return ItemMapper.buildItemDtoList(searchItems);
    }

    @Override
    public RespCommentDto createComment(Long userId, Long itemId, ReqCommentDto commentDto) {
        User dbUser = checkIfUserExistsById(userId);
        Item dbItem = checkIfItemExistsById(itemId);
        String text = commentDto.getText();

        PageRequest page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "end"));
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId,
                itemId,
                Status.APPROVED,
                LocalDateTime.now(),
                page);

        if (bookings.isEmpty()) {
            throw new IncorrectBookingException(String.format("User c id=%d ещё не брал Item c id=%d в аренду", userId, itemId));
        }

        Comment comment = CommentMapper.buildComment(text, dbItem, dbUser);
        Comment dbComment = commentRepository.save(comment);
        log.info("POST запрос в ItemController обработан успешно. Метод createComment(), userId={}, itemId={}, text={} ", userId, itemId, text);

        return CommentMapper.buildCommentDto(dbComment);
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User dbUser = checkIfUserExistsById(userId);

        Item item = ItemMapper.buildItem(dbUser, itemDto);
        Item dbItem = itemRepository.save(item);

        dbUser.addItem(dbItem);
        log.info("POST запрос в ItemController обработан успешно. Метод createItem(), createItem={}", dbItem);

        return ItemMapper.buildItemDto(dbItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item dbItem = checkIfItemExistsById(itemId);
        User dbItemUser = dbItem.getUser();

        boolean isItemOfUser = Objects.equals(dbItemUser.getId(), userId);
        if (!isItemOfUser) {
            throw new NotFoundException("У пользователя нет вещи c таким идентификатором: " + itemId);

        }

        boolean isName = itemDto.getName() != null;
        boolean isDescription = itemDto.getDescription() != null;
        boolean isAvailable = itemDto.getAvailable() != null;

        if (isName) {
            dbItem.setName(itemDto.getName());
        }

        if (isDescription) {
            dbItem.setDescription(itemDto.getDescription());
        }

        if (isAvailable) {
            dbItem.setAvailable(itemDto.getAvailable());
        }

        Item updateItem = itemRepository.save(dbItem);
        log.info("PATCH запрос в ItemController обработан успешно. Метод updateItem(), itemId={}, updateItem={}", itemId, updateItem);

        return ItemMapper.buildItemDto(updateItem);
    }

    private User checkIfUserExistsById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с таким id=%d нет", + userId)));
    }

    private Item checkIfItemExistsById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещи с таким id=%d нет", + itemId)));
    }
}
