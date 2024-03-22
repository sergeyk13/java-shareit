package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> requestCreate(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("\n________________________________________________");
        log.info("Create request from user {}, Request {}", userId, requestDto);
        return client.requestCreate(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByUser(
            @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("\n________________________________________________");
        log.info("Get request by user: {}", userId);
        return client.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("\n________________________________________________");
        log.info("Get all request,by user: {}", userId);
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable long requestId) {
        log.info("\n________________________________________________");
        log.info("Get request: {}, by user: {}", requestId, userId);
        return client.getRequestById(userId, requestId);
    }
}
