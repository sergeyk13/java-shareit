package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

public interface ItemRepository {
    ItemDto itemCreate(ItemDto itemDto, long userId);

    ItemDto getItem(long itemId);

    List<ItemDto> findItemByOwnerId(long userId);

    List<ItemDto> getAll();

    ItemDto addUpdatingItem(long itemId, Item item);

    Item updateItem(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest);

    boolean removeItem(long itemId);

    List<ItemDto> searchItems(String searchText);
}
