package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

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
    public ResponseEntity<List<BookingDtoResponse>> getBookingByState(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.getBookingByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoResponse>> getBookingForOwnerByState(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.getBookingForOwnerByState(userId, state, from, size);
    }
}
