package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDtoResponse bookingCreate(long userId, BookingDto bookingDto);

    BookingDtoResponse bookingApprove(long userId, long bookingId, boolean approved);

    BookingDtoResponse getBookingById(long userId, long bookingId);

    List<BookingDtoResponse> getBookingByState(long userId, BookingState state, int from, int size);

    List<BookingDtoResponse> getBookingForOwnerByState(long userId, BookingState state, int from, int size);

    List<BookingDtoResponse> getAll(int from, int size);
}
