package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    // Дальше посмотрим по тестам, какие поля добавить.
    private Long id;
    private String name;
    @Email
    private String email;
    private final Set<Long> items = new HashSet<>();

    public void addItem(Long itemId) {
        items.add(itemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
