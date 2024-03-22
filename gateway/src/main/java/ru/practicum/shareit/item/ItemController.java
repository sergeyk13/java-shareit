package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdatingRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> itemCreate(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("\n________________________________________________");
        log.info("Create item from user {}, ItemDto {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("\n________________________________________________");
        log.info("Get item by user: {}", userId);
        return itemClient.getAllByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId,
                                             @RequestBody ItemUpdatingRequest itemUpdatingRequest) {
        log.info("\n________________________________________________");
        log.info("Update item: {} by user: {}, ItemUpdating: {}", itemId, userId, itemUpdatingRequest);
        return itemClient.updateItem(userId, itemId, itemUpdatingRequest);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> removeItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        log.info("\n________________________________________________");
        log.info("Remove item: {} by user: {}", itemId, userId);
        return itemClient.removeItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam String text,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("\n________________________________________________");
        log.info("Search item include: {} ,by user: {}", text, userId);
        return itemClient.searchItems(userId, text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByIdWithDate(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        log.info("\n________________________________________________");
        log.info("Get item: {}, by user: {}", itemId, userId);
        return itemClient.getItemByIdWithDate(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CommentDto text) {
        log.info("\n________________________________________________");
        log.info("Create comment: {}, by item: {} from user {}", text, itemId, userId);
        return itemClient.createComment(userId, itemId, text);
    }
}
