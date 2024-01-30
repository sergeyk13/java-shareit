package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface ItemMapperInt {
    ItemMapperInt INSTANCE = Mappers.getMapper(ItemMapperInt.class);

    @Mapping(target = "authorName", source = "user.name")
    CommentDtoResponse modelToDto(Comment comment);
}
