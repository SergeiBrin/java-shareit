package ru.practicum.shareit.user.repository;

import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Data
public class UserRepositoryImpl implements UserRepository {
    private long id;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(++id);
        users.put(id, user);

        return users.get(id);
    }

    @Override
    public User deleteUserById(Long userId) {
        return users.remove(userId);
    }
}
