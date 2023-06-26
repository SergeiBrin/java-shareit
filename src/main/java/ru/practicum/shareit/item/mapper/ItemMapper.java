package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.LongItemDto;
import ru.practicum.shareit.item.model.dto.RespCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item buildItem(User dbUser, ItemDto itemDto, ItemRequest itemRequest) {

        return Item.builder()
                .user(dbUser)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemRequest)
                .build();
    }

    public static ItemDto buildItemDto(Item item) {
        Long itemRequest = null;
        if (item.getRequest() != null) {
            itemRequest = item.getRequest().getId();
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(itemRequest)
                .build();
    }

    public static LongItemDto buildLongItemDto(Item item,
                                               List<Booking> lastBooking,
                                               List<Booking> nextBooking,
                                               List<RespCommentDto> comments) {
        Booking last = null;
        Booking next = null;
        if (!lastBooking.isEmpty()) {
            last = lastBooking.get(0);
        }
        if (!nextBooking.isEmpty()) {
            next = nextBooking.get(0);
        }

        Long itemRequest = null;
        if (item.getRequest() != null) {
            itemRequest = item.getRequest().getId();
        }

        return LongItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(getBookingInfo(last))
                .nextBooking(getBookingInfo(next))
                .comments(comments)
                .requestId(itemRequest)
                .build();

    }

    public static List<ItemDto> buildItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::buildItemDto)
                .collect(Collectors.toList());
    }

    public static List<LongItemDto> buildLongItemDtoList(List<Item> items,
                                                         Map<Item, Booking> last,
                                                         Map<Item, Booking> next,
                                                         Map<Item, List<Comment>> comments) {

        List<LongItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            List<RespCommentDto> commentDtos = new ArrayList<>();
            List<Booking> lastBooking = new ArrayList<>();
            List<Booking> nextBooking = new ArrayList<>();

            if (last.containsKey(item)) {
                lastBooking.add(last.get(item));
            }
            if (next.containsKey(item)) {
                nextBooking.add(next.get(item));
            }
            if (comments.containsKey(item)) {
                 commentDtos = CommentMapper.buildCommentDtoList(comments.getOrDefault(item, Collections.emptyList()));
            }

            itemDtos.add(buildLongItemDto(item, lastBooking, nextBooking, commentDtos));
        }

        return itemDtos;
    }

    private static BookingInfo getBookingInfo(Booking booking) {
        if (booking == null) {
            return null;
        }

        Long id = booking.getId();
        Long bookerId = booking.getBooker().getId();

        return new BookingInfo(id, bookerId);
    }
}

