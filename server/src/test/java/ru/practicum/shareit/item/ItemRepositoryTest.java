package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.FactoryEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testSearchItems() {
        User user = FactoryEntity.creatRandomUserWithoutId();
        assertNull(user.getId());
        testEntityManager.persist(user);
        assertNotNull(user.getId());

        Item item1 = FactoryEntity.createRandomItemWithOutId(user.getId());
        Item item2 = FactoryEntity.createRandomItemWithOutId(user.getId());
        item1.setName("search");
        item2.setDescription("search");

        assertNull(item1.getId());
        assertNull(item2.getId());

        testEntityManager.persist(item1);
        testEntityManager.persist(item2);

        String searchText = "search";
        List<Item> items = itemRepository.searchItems(searchText);

        assertEquals(2, items.size());
    }
}
