package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
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
        if (getItem == null) {
            throw new NotFoundException("Вещи с таким id нет: " + itemId);
        }

        log.info("GET запрос в ItemController обработан успешно. Метод getItemById(), itemId={}, Item={}", itemId, getItem);

        return itemMapper.buildItemDto(getItem);
    }

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        checkIfUserExistsById(userId);

        List<Item> allItems = itemRepository.getAllItems();

        List<Item> itemsByUser = allItems.stream()
                .filter(item -> Objects.equals(item.getUserId(), userId))
                .collect(Collectors.toList());
        log.info("GET запрос в ItemController обработан успешно. Метод getItemsByUser(), userId={}, itemsByUser={}", userId, itemsByUser);

        return itemMapper.buildItemDtoList(itemsByUser);
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        if (text.isEmpty()) {
            log.info("GET запрос в ItemController не обработан, так как text пуст. Метод searchAvailableItems(), text={}", text);
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase();
        List<Item> allItems = itemRepository.getAllItems();

        List<Item> searchItems  = allItems.stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> (item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
        log.info("GET запрос в ItemController обработан успешно. Метод searchAvailableItems(), text={}, searchItems={}", searchText, searchItems);

        return itemMapper.buildItemDtoList(searchItems);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = checkIfUserExistsById(userId);

        Item item = itemMapper.buildItem(userId, ++itemId, itemDto);
        Item createItem = itemRepository.createItem(itemId, item);

        user.addItem(itemId);

        log.info("POST запрос в ItemController обработан успешно. Метод createItem(), createItem={}", createItem);

        return itemMapper.buildItemDto(createItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = checkIfUserExistsById(userId);
        Item updateItem = checkIfItemExistsById(itemId);
        checkThatItemBelongsToUser(user, itemId);

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

    private User checkIfUserExistsById(Long userId) {
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователя с таким id нет: " + userId);
        }

        return user;
    }

    private Item checkIfItemExistsById(Long itemId) {
        Item item = itemRepository.getItemById(itemId);

        if (item == null) {
            throw new NotFoundException("Вещи с таким id нет: " + itemId);
        }

        return item;
    }

    private void checkThatItemBelongsToUser(User user, Long itemId) {
        boolean isItemOfUser = user.getItems().contains(itemId);

        if (!isItemOfUser) {
            throw new NotFoundException("У пользователя нет вещи c таким идентификатором: " + itemId);
        }
    }
}
