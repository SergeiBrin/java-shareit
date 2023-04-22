package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private long itemId;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getItemById(Long itemId) {
        Item getItem = itemRepository.getItemById(itemId);
        log.info("GET запрос в ItemController обработан успешно. Метод getItemById(), itemId={}, Item={} ", itemId, getItem);

        return itemMapper.buildItemDto(getItem);
    }

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        List<Item> allItems = itemRepository.getAllItems();

        List<Item> itemsByUser = allItems.stream()
                .filter(item -> Objects.equals(item.getUserId(), userId))
                .collect(Collectors.toList());
        log.info("GET запрос в ItemController обработан успешно. Метод getItemsByUser(), userId={}, itemsByUser={} ", userId, itemsByUser);

        return itemMapper.buildItemDtoList(itemsByUser);
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        String searchText = text.toLowerCase();
        List<Item> allItems = itemRepository.getAllItems();

        List<Item> searchItems  = allItems.stream()
                .filter(item ->
                    item.getAvailable().equals(true)
                    && (item.getName().toLowerCase().contains(searchText)
                    || item.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
        log.info("GET запрос в ItemController обработан успешно. Метод searchAvailableItems(), text={}, searchItems={} ", searchText, searchItems);

        return itemMapper.buildItemDtoList(searchItems);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = itemMapper.buildItem(userId, ++itemId, itemDto);
        Item createItem = itemRepository.createItem(itemId, item);

        User user = userRepository.getUserById(userId);
        user.addItem(itemId);

        log.info("POST запрос в ItemController обработан успешно. Метод createItem(), createItem={} ", createItem);

        return itemMapper.buildItemDto(createItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.getItemById(itemId);

        boolean isName = itemDto.getName() != null;
        boolean isDescription = itemDto.getDescription() != null;
        boolean isAvailable = itemDto.getAvailable() != null;

        if (isName) {
            updateItem.setName(itemDto.getName());
        }

        if (isDescription) {
            updateItem.setDescription(itemDto.getDescription());
        }

        if (isAvailable) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        log.info("PATCH запрос в ItemController обработан успешно. Метод updateItem(), itemId={}, updateItem={}", itemId, updateItem);

        return itemMapper.buildItemDto(itemRepository.getItemById(itemId));
    }
}
