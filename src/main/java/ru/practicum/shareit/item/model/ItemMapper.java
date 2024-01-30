package ru.practicum.shareit.item.model;

import ru.practicum.shareit.booking.dto.BookingDtoByItem;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemDto itemToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static ItemDto itemToItemDto(Optional<Item> itemOptional) {
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    public static List<ItemDto> itemToItemDtoList(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(itemToItemDto(item));
        }

        return result;
    }

    public static Item itemDtoToItem(ItemDto itemDto, long ownerId) {
        Item item = new Item();
        item.setOwnerId(ownerId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemResponseDto mapToItemResponseDto(Item item, BookingDtoByItem last,
                                                       BookingDtoByItem next, List<CommentDtoResponse> comments) {
        return new ItemResponseDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), last, next,
                comments);
    }
}
