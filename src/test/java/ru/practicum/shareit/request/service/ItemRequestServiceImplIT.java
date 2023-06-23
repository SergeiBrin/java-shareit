package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
// @Rollback(false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIT {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final User user1 = new User();
    private final User user2 = new User();
    private final ReqItemRequestDto requestDto1 = new ReqItemRequestDto();
    private final ReqItemRequestDto requestDto2 = new ReqItemRequestDto();

    @BeforeEach
    void setUp() {
        user1.setId(0L);
        user1.setName("User1");
        user1.setEmail("user1@gmail.com");

        user2.setId(0L);
        user2.setName("User2");
        user2.setEmail("user2@gmail.com");

        requestDto1.setDescription("I need Play Station 5");
        requestDto2.setDescription("I need Xbox");
    }

    // Сбрасываю поля id в таблицах, чтобы их id каждый раз были равны 1
    @AfterEach
    void tearDown() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Test
    void getUserRequests_ShouldReturnUserRequestsListFromDataBase() {
        User dbUser = userService.createUser(user1);
        itemRequestService.createRequest(dbUser.getId(), requestDto1);
        itemRequestService.createRequest(dbUser.getId(), requestDto2);

        TypedQuery<ItemRequest> query = em.createQuery(
                "select ir from ItemRequest as ir join ir.creator as cr where cr.id = :creatorId",
                ItemRequest.class);
        query.setParameter("creatorId", dbUser.getId());

        List<ItemRequest> dbItemRequests = query.getResultList();
        assertThat(dbItemRequests, hasSize(2));

        ItemRequest itemRequest1 = dbItemRequests.get(0);
        ItemRequest itemRequest2 = dbItemRequests.get(1);

        assertThat(itemRequest1.getId(), equalTo(1L));
        assertThat(itemRequest1.getCreator(), equalTo(dbUser));
        assertThat(itemRequest1.getDescription(), equalTo("I need Play Station 5"));
        assertThat(itemRequest1.getItems(), is(empty()));

        assertThat(itemRequest2.getId(), equalTo(2L));
        assertThat(itemRequest2.getCreator(), equalTo(dbUser));
        assertThat(itemRequest2.getDescription(), equalTo("I need Xbox"));
        assertThat(itemRequest2.getItems(), is(empty()));
    }

    @Test
    void getRequestByReqId_ShouldReturnValidItemRequestFromDataBase() {
        User dbUser = userService.createUser(user1);
        itemRequestService.createRequest(dbUser.getId(), requestDto1);
        itemRequestService.createRequest(dbUser.getId(), requestDto2);

        TypedQuery<ItemRequest> query = em.createQuery(
                "select ir from ItemRequest as ir where ir.id = :id",
                ItemRequest.class);
        query.setParameter("id", 2L);
        ItemRequest dbItemRequest = query.getSingleResult();

        assertThat(dbItemRequest.getId(), equalTo(2L));
        assertThat(dbItemRequest.getCreator(), equalTo(dbUser));
        assertThat(dbItemRequest.getDescription(), equalTo("I need Xbox"));
        assertThat(dbItemRequest.getItems(), is(empty()));
    }

    @Test
    void getRequestsFromOthers_ShouldReturnItemRequestListFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemRequestService.createRequest(dbUser1.getId(), requestDto1);
        itemRequestService.createRequest(dbUser2.getId(), requestDto2);

        TypedQuery<ItemRequest> query = em.createQuery(
                "select ir from ItemRequest as ir join ir.creator as cr where cr.id <> :creatorId",
                ItemRequest.class);
        query.setParameter("creatorId", dbUser1.getId());

        List<ItemRequest> dbItemRequests = query.getResultList();
        assertThat(dbItemRequests, hasSize(1));

        ItemRequest itemRequest = dbItemRequests.get(0);
        assertThat(itemRequest.getId(), equalTo(2L));
        assertThat(itemRequest.getCreator(), equalTo(dbUser2));
        assertThat(itemRequest.getDescription(), equalTo("I need Xbox"));
        assertThat(itemRequest.getItems(), is(empty()));
    }

    @Test
    void createRequest_ShouldCreatedItemRequestFromDataBase() {
        User dbUser = userService.createUser(user1);
        itemRequestService.createRequest(dbUser.getId(), requestDto1);

        TypedQuery<ItemRequest> query = em.createQuery(
                "select ir from ItemRequest as ir where ir.id = :id",
                ItemRequest.class);
        query.setParameter("id", 1L);
        ItemRequest dbItemRequest = query.getSingleResult();

        assertThat(dbItemRequest.getId(), equalTo(1L));
        assertThat(dbItemRequest.getCreator(), equalTo(dbUser));
        assertThat(dbItemRequest.getDescription(), equalTo("I need Play Station 5"));
        assertThat(dbItemRequest.getItems(), is(empty()));
    }
}