package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

public interface ItemService {
    ItemDto itemCreate(long userId, ItemDto itemDto);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getAll();

    List<ItemDto> getAllByUserId(long userId);

    ItemDto updateItem(long userId, long itemId, Item item);

    Item prepareUpdating(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest);

    void removeItem(long userId, long itemId);

    List<ItemDto> searchItems(String searchText);
}
