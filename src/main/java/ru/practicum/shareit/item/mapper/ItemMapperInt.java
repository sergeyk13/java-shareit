package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDtoByItem;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper
public interface ItemMapperInt {
    ItemMapperInt INSTANCE = Mappers.getMapper(ItemMapperInt.class);

    @Mapping(target = "authorName", source = "user.name")
    CommentDtoResponse modelCommentToDto(Comment comment);

    ItemDto modelToDto(Item item);
    @Mapping(target = "id", ignore = true)
    Item dtoToModel(ItemDto itemDto, long ownerId);

    List<ItemDto> mapModelListToDtoList(Iterable<Item> items);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    ItemResponseDto mapToItemResponseDto(Item item, BookingDtoByItem lastBooking,
                                         BookingDtoByItem nextBooking, List<CommentDtoResponse> comments);
}
