package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static ItemDto itemToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item createItem(ItemDto itemDto, long userId, long id) {
        return new Item(id, userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
