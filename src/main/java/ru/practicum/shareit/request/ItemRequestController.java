package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> requestCreate(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestBody @Valid ItemRequestDto requestDto) {
        return service.requestCreate(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> getRequestByUser(
            @RequestHeader(X_SHARER_USER_ID) long userId) {
        return service.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoResponse>> getAllRequests(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> getRequestById(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable long requestId) {
        return service.getRequestById(userId, requestId);
    }
}
