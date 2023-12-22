package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

public interface ItemRepository {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> findItemByOwnerId(long userId);

    List<ItemDto> findAll();

    ItemDto addUpdatingItem(long itemId, Item item);

    Item updatingItem(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest);

    boolean removeItem(long itemId);

    List<ItemDto> searchItems(String searchText);
}
