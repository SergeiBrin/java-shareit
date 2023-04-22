package ru.practicum.shareit.item.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;

@Component
@RequiredArgsConstructor
public class ItemValidator {
    private final ItemRepositoryImpl repository;

    public void checkIfItemExistsById(Long itemId) {
        boolean isNotFound = repository.getItemById(itemId) == null;

        if (isNotFound) {
            throw new ItemNotFoundException("Вещи с таким id нет: " + itemId);
        }
    }
}
