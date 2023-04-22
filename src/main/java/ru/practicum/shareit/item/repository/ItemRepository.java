package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItemById(Long itemId);

    List<Item> getAllItems();

    Item createItem(Long itemId, Item item);
}
