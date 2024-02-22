package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(long userId, ItemDto itemDto);

    ItemDto getItem(long userId, long itemId);

    List<ItemResponseDto> getAllByUserId(long userId);

    ItemDto updateItem(long userId, long itemId, Item item);

    void removeItem(long userId, long itemId);

    List<ItemDto> searchItems(String searchText, int from, int size);

    Item prepareUpdating(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest);

    ItemResponseDto getItemByIdWithDate(Long itemId, long userId);

    CommentDtoResponse createComment(long userId, long itemId, CommentDto text);
}
