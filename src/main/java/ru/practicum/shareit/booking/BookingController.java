package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import java.util.LinkedHashSet;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> bookingCreate(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        return service.bookingCreate(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> bookingApprove(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return service.bookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable long bookingId) {
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<LinkedHashSet<BookingDtoResponse>> getBookingByState(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return service.getBookingByState(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<LinkedHashSet<BookingDtoResponse>> getBookingForOwnerByState(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return service.getBookingForOwnerByState(userId, state);
    }
}
