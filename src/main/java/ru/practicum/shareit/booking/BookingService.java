package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.LinkedHashSet;
import java.util.List;

public interface BookingService {
    ResponseEntity<BookingDtoResponse> bookingCreate(long userId, BookingDto bookingDto);

    ResponseEntity<BookingDtoResponse> bookingApprove(long userId, long bookingId, boolean approved);

    ResponseEntity<BookingDtoResponse> getBookingById(long userId, long bookingId);

    ResponseEntity<List<BookingDtoResponse>> getBookingByState(long userId, BookingState state);

    ResponseEntity<List<BookingDtoResponse>> getBookingForOwnerByState(long userId, BookingState state);
}
