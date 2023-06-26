package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageRequestFactoryTest {

    @Test
    void createPageRequest_ShouldReturnValidPageRequest() {
        Pageable page = PageRequestFactory.createPageRequest(10, 5, Sort.by("id"));

        assertThat(page, is(instanceOf(Pageable.class)));
        assertThat(page.getPageNumber(), is(2));
        assertThat(page.getPageSize(), is(5));
        assertThat(page.getSort(), is(Sort.by("id")));
    }

    @Test
    void createPageRequest_ShouldReturnIllegalArgumentExceptionForPage() {
        final IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> PageRequestFactory.createPageRequest(-1, 5, Sort.by("id")));

        assertThat(IllegalArgumentException.class, is(e.getClass()));
    }

    @Test
    void createPageRequest_ShouldReturnIllegalArgumentExceptionForSize() {
        final IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> PageRequestFactory.createPageRequest(0, 0, Sort.by("id")));

        assertThat(IllegalArgumentException.class, is(e.getClass()));
    }
}