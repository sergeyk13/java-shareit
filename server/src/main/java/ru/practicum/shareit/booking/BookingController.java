package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse bookingCreate(@RequestHeader(X_SHARER_USER_ID) long userId,
                                            @RequestBody @Valid BookingDto bookingDto) {
        return service.bookingCreate(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse bookingApprove(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        return service.bookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable long bookingId) {
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoResponse> getBookingByState(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return service.getBookingByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingForOwnerByState(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
                                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                              @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return service.getBookingForOwnerByState(userId, state, from, size);
    }
}
