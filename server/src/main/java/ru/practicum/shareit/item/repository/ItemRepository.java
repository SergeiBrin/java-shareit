package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUserId(Long userId, Pageable page);

    Optional<Item> findByIdAndUserIdNot(Long itemId, Long userId);

    @Query("select it " +
           "from Item as it " +
           "where (upper(it.name) like upper('%'||?1||'%') " +
           "or upper(it.description) like upper('%'||?1||'%')) " +
           "and it.available = true")
    List<Item> findByText(String text, Pageable page);
}
