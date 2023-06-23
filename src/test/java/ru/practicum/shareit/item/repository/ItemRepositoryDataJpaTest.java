package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PageRequestFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRepositoryDataJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByText_ShouldSaveAndReturnItemFromDataBase() {
        User user = new User(null, "User", "user@gmail.com");
        Item item = new Item(null, user, "Item name", "Item description", true, null);

        Assertions.assertNull(item.getId());
        em.persist(user);
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        List<Item> items = itemRepository.findByText(
                "name", PageRequestFactory.createPageRequest(0, 10, Sort.by("id")));
        assertThat(items, hasSize(1));

        Item dbItem = items.get(0);

        assertThat(dbItem.getId(), equalTo(1L));
        assertThat(dbItem.getName(), equalTo(item.getName()));
        assertThat(dbItem.getDescription(), equalTo(item.getDescription()));
        assertThat(dbItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(dbItem.getUser(), equalTo(user));
        assertThat(dbItem.getRequest(), equalTo(item.getRequest()));
    }
}