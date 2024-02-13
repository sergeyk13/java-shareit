package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    ItemRequest dtoToModel(ItemRequestDto requestDto);

    ItemRequestDtoResponse modelToResponseDto(ItemRequest itemRequest);

    @Mapping(target = "ItemResponseToRequestDto.requestId", source = "item.requestId")
    List<ItemRequestDtoResponse> modelListToResponseDtoList(List<ItemRequest> itemRequestList);

    ItemRequestDto modelToDto (ItemRequest itemRequest);
}
