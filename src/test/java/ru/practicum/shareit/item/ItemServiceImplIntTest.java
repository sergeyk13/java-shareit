package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapperInt;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.utils.FactoryEntity;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntTest {
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void getAllByUserId() {
        UserDto userDto = FactoryEntity.creatRandomUserDto();
        User user = UserMapper.INSTANCE.dtoToModel(userDto);
        em.persist(user);

        ItemDto itemDto = FactoryEntity.createRandomItemDto(1L);
        Item item1 = ItemMapperInt.INSTANCE.dtoToModel(itemDto, user.getId());
        em.persist(item1);

        ItemDto itemDto2 = FactoryEntity.createRandomItemDto(2L);
        Item item2 = ItemMapperInt.INSTANCE.dtoToModel(itemDto2, user.getId());
        em.persist(item2);

        List<ItemResponseDto> items = itemService.getAllByUserId(user.getId());

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), notNullValue());
        assertThat(items.get(0).getName(), equalTo(item1.getName()));
        assertThat(items.get(1).getId(), notNullValue());
        assertThat(items.get(1).getName(), equalTo(item2.getName()));
    }
}