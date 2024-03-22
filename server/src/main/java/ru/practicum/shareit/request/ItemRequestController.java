package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
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
    public ItemRequestDto requestCreate(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestBody @Valid ItemRequestDto requestDto) {
        return service.requestCreate(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getRequestByUser(
            @RequestHeader(X_SHARER_USER_ID) long userId) {
        return service.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllRequests(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestById(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable long requestId) {
        return service.getRequestById(userId, requestId);
    }
}
