package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoByItem;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", expression = "java( ru.practicum.shareit.booking.model.BookingState.WAITING)")
    Booking dtoToModel(BookingDto bookingDto);

    BookingDtoResponse modelToResponse(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoByItem modelToBookingByItem(Booking booking);
}
