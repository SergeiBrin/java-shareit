package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public Item buildItem(Long userId, Long itemId, ItemDto itemDto) {
        return Item.builder()
                .itemId(itemId)
                .userId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable()) // Возвращает значение available
                .build();
    }

    public ItemDto buildItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getItemId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable()) // Возвращает значение available
                .build();
    }

    public List<ItemDto> buildItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::buildItemDto)
                .collect(Collectors.toList());
    }
}
