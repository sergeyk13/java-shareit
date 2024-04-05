package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(long userId);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE CONCAT('%',LOWER(?1), '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', LOWER(?1), '%')) AND i.available = true ")
    List<Item> searchItems(String searchText);
}
