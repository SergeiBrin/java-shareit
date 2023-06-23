package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
// @Rollback(false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    private final EntityManager em;
    private final UserService userService;
    private final User user1 = new User();
    private final User user2 = new User();


    @BeforeEach
    void setUp() {
        user1.setId(0L);
        user1.setName("User1");
        user1.setEmail("user1@gmail.com");
        user2.setId(0L);
        user2.setName("User2");
        user2.setEmail("user2@gmail.com");
    }

    // Сбрасываю поле id в таблице, чтобы id каждый раз был равен 1
    @AfterEach
    void tearDown() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Test
    void getUserById_ShouldReturnValidUserFromDataBase() {
        userService.createUser(user1);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.id = :id", User.class);
        query.setParameter("id", 1L);
        User dbUser = query.getSingleResult();

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("User1"));
        assertThat(dbUser.getEmail(), equalTo("user1@gmail.com"));
        assertThat(dbUser.getItems(), is(empty()));

    }

    @Test
    void getAllUsers_ShouldReturnUserListFromDataBase() {
        userService.createUser(user1);
        userService.createUser(user2);

        user1.setId(1L);
        user2.setId(2L);

        TypedQuery<User> query = em.createQuery("select u from User as u", User.class);
        List<User> dbUsers = query.getResultList();

        assertThat(dbUsers, hasSize(2));
        assertThat(dbUsers, hasItems(user1, user2));

        User dbUser1 =  dbUsers.get(0);
        User dbUser2 =  dbUsers.get(1);

        assertThat(dbUser1.getId(), equalTo(1L));
        assertThat(dbUser1.getName(), equalTo("User1"));
        assertThat(dbUser1.getEmail(), equalTo("user1@gmail.com"));
        assertThat(dbUser1.getItems(), is(empty()));

        assertThat(dbUser2.getId(), equalTo(2L));
        assertThat(dbUser2.getName(), equalTo("User2"));
        assertThat(dbUser2.getEmail(), equalTo("user2@gmail.com"));
        assertThat(dbUser2.getItems(), is(empty()));

    }

    @Test
    void createUser_ShouldCreatedUserFromDataBase() {
        userService.createUser(user1);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.email = :email", User.class);
        User dbUser = query.setParameter("email", user1.getEmail()).getSingleResult();

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("User1"));
        assertThat(dbUser.getEmail(), equalTo("user1@gmail.com"));
        assertThat(dbUser.getItems(), is(empty()));
    }

    @Test
    void updateUser_ShouldUpdatedUserFromDataBase() {
        userService.createUser(user1);
        userService.updateUser(1L, user2);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.id = :id", User.class);
        query.setParameter("id", 1L);
        User updateUser = query.getSingleResult();

        assertThat(updateUser.getId(), equalTo(1L));
        assertThat(updateUser.getName(), equalTo("User2"));
        assertThat(updateUser.getEmail(), equalTo("user2@gmail.com"));
        assertThat(updateUser.getItems(), is(empty()));
    }

    @Test
    void deleteUserById_ShouldDeleteUserFromDataBase() {
        userService.createUser(user1);

        em.createQuery("delete from User u where u.id = :id")
                .setParameter("id", 1L)
                .executeUpdate();

        // Очистка кэша.
        // Теперь последующие запросы к базе данных не будут использовать закэшированные данные.
        em.clear();
        User deletedUser = em.find(User.class, 1L);

        assertThat(deletedUser, is(nullValue()));
    }
}