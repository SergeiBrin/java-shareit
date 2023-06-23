package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIT {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final User user1 = new User();
    private final User user2 = new User();
    private final ItemDto itemDto1 = new ItemDto();
    private final ItemDto itemDto2 = new ItemDto();
    private final ReqBookingDto reqBookingDto = new ReqBookingDto();
    private final ReqCommentDto reqCommentDto = new ReqCommentDto();

    @BeforeEach
    void setUp() {
        user1.setId(0L);
        user1.setName("User1");
        user1.setEmail("user1@gmail.com");

        user2.setId(0L);
        user2.setName("User2");
        user2.setEmail("user2@gmail.com");

        itemDto1.setId(0L);
        itemDto1.setName("Item1 name");
        itemDto1.setDescription("Item1 description");
        itemDto1.setAvailable(true);

        itemDto2.setId(0L);
        itemDto2.setName("Item2 name");
        itemDto2.setDescription("Item2 description");
        itemDto2.setAvailable(true);

        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.now());
        reqBookingDto.setEnd(LocalDateTime.now().plusNanos(100)); // чтобы оставить comment на вещь. Service возвращает endBefore.

        reqCommentDto.setText("Классная вещь");
    }

    // Сбрасываю поля id в таблицах, чтобы их id каждый раз были равны 1
    @AfterEach
    void tearDown() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Test
    void getItemById_ShouldReturnValidItemsFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        itemService.createItem(dbUser1.getId(), itemDto1);
        itemService.createItem(dbUser1.getId(), itemDto2);

        TypedQuery<Item> query = em.createQuery("select it from Item as it where it.id = :id", Item.class);
        query.setParameter("id", 1L);
        Item dbItem1 = query.getSingleResult();
        query.setParameter("id", 2L);
        Item dbItem2 = query.getSingleResult();

        assertThat(dbItem1.getId(), equalTo(1L));
        assertThat(dbItem1.getName(), equalTo("Item1 name"));
        assertThat(dbItem1.getDescription(), equalTo("Item1 description"));
        assertThat(dbItem1.getAvailable(),  equalTo(true));
        assertThat(dbItem1.getRequest(), is(nullValue()));

        assertThat(dbItem2.getId(), equalTo(2L));
        assertThat(dbItem2.getName(), equalTo("Item2 name"));
        assertThat(dbItem2.getDescription(), equalTo("Item2 description"));
        assertThat(dbItem2.getAvailable(), equalTo(true));
        assertThat(dbItem2.getRequest(), is(nullValue()));
    }

    @Test
    void getAllItemsByUser_ShouldReturnItemListFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        itemService.createItem(dbUser1.getId(), itemDto1);
        itemService.createItem(dbUser1.getId(), itemDto2);

        TypedQuery<Item> query = em.createQuery(
                "select it from Item as it join it.user as u where u.id = :userId",
                Item.class);
        query.setParameter("userId", dbUser1.getId());

        List<Item> dbItems = query.getResultList();
        assertThat(dbItems, hasSize(2));

        Item dbItem1 = dbItems.get(0);
        Item dbItem2 = dbItems.get(1);

        assertThat(dbItem1.getId(), equalTo(1L));
        assertThat(dbItem1.getName(), equalTo("Item1 name"));
        assertThat(dbItem1.getDescription(), equalTo("Item1 description"));
        assertThat(dbItem1.getAvailable(),  equalTo(true));
        assertThat(dbItem1.getRequest(), is(nullValue()));

        assertThat(dbItem2.getId(), equalTo(2L));
        assertThat(dbItem2.getName(), equalTo("Item2 name"));
        assertThat(dbItem2.getDescription(), equalTo("Item2 description"));
        assertThat(dbItem2.getAvailable(), equalTo(true));
        assertThat(dbItem2.getRequest(), is(nullValue()));
    }

    @Test
    void searchAvailableItems_ShouldReturnItemListFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        itemService.createItem(dbUser1.getId(), itemDto1);
        itemService.createItem(dbUser1.getId(), itemDto2);

        TypedQuery<Item> query = em.createQuery(
                "select it " +
                "from Item as it " +
                "where (upper(it.name) like upper('%'||:text||'%') " +
                "or upper(it.description) like upper('%'||:text||'%')) " +
                "and it.available = true",
                Item.class);
        query.setParameter("text", "ItEm1");

        List<Item> searchItems = query.getResultList();
        assertThat(searchItems, hasSize(1));

        Item searchItem = searchItems.get(0);
        assertThat(searchItem.getId(), equalTo(1L));
        assertThat(searchItem.getName(), equalTo("Item1 name"));
        assertThat(searchItem.getDescription(), equalTo("Item1 description"));
        assertThat(searchItem.getAvailable(),  equalTo(true));
        assertThat(searchItem.getRequest(), is(nullValue()));
    }

    @Test
    void createComment_ShouldReturnCreatedCommentFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        ItemDto dbItem = itemService.createItem(dbUser1.getId(), itemDto1);

        bookingService.createBooking(dbUser2.getId(), reqBookingDto);
        bookingService.updateBooking(dbUser1.getId(), dbItem.getId(), true);
        itemService.createComment(dbUser2.getId(), dbItem.getId(), reqCommentDto);

        TypedQuery<Comment> query = em.createQuery(
                "select com " +
                   "from Comment as com " +
                   "join com.item as it " +
                   "join com.author as au " +
                   "where it.id = :itemId " +
                   "and au.id = :authorId",
                Comment.class);
        query.setParameter("itemId", dbItem.getId());
        query.setParameter("authorId", dbUser2.getId());

        List<Comment> comments = query.getResultList();
        assertThat(comments, hasSize(1));

        Comment comment = comments.get(0);
        assertThat(comment.getId(), equalTo(1L));
        assertThat(comment.getItem().getId(), equalTo(1L));
        assertThat(comment.getText(), equalTo("Классная вещь"));
        assertThat(comment.getAuthor(), equalTo(dbUser2));
        assertThat(comment.getCreated(), is(notNullValue()));
    }

    @Test
    void createItem_ShouldCreatedItemFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        itemService.createItem(dbUser1.getId(), itemDto1);

        TypedQuery<Item> query = em.createQuery("select it from Item as it where it.id = :id", Item.class);
        query.setParameter("id", 1L);
        Item dbItem = query.getSingleResult();

        assertThat(dbItem.getId(), equalTo(1L));
        assertThat(dbItem.getName(), equalTo("Item1 name"));
        assertThat(dbItem.getDescription(), equalTo("Item1 description"));
        assertThat(dbItem.getAvailable(),  equalTo(true));
        assertThat(dbItem.getRequest(), is(nullValue()));
    }

    @Test
    void updateItem_ShouldUpdatedItemFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        ItemDto dbItemDto = itemService.createItem(dbUser1.getId(), itemDto1);
        dbItemDto.setName("Update name");
        dbItemDto.setDescription("Update description");
        itemService.updateItem(dbUser1.getId(), dbItemDto.getId(), dbItemDto);

        TypedQuery<Item> query = em.createQuery("select it from Item as it where it.id = :id", Item.class);
        query.setParameter("id", dbItemDto.getId());
        Item updateItem = query.getSingleResult();

        assertThat(updateItem.getId(), equalTo(1L));
        assertThat(updateItem.getUser(), equalTo(dbUser1));
        assertThat(updateItem.getName(), equalTo("Update name"));
        assertThat(updateItem.getDescription(), equalTo("Update description"));
        assertThat(updateItem.getRequest(), is(nullValue()));
    }
}