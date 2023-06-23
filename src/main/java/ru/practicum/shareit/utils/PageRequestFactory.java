package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestFactory {

    public static PageRequest createPageRequest(int from, int size, Sort sort) {
        if (from < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }

        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}
