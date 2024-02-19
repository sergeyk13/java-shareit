package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto itemCreate(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid ItemDto itemDto) {
        return service.saveItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemResponseDto> getAllByUserId(@RequestHeader(X_SHARER_USER_ID) long userId) {
        return service.getAllByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId,
                              @RequestBody ItemUpdatingRequest itemUpdatingRequest) {
        Item item = service.prepareUpdating(userId, itemId, itemUpdatingRequest);
        return service.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        service.removeItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.searchItems(text, from,size);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemByIdWithDate(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        return service.getItemByIdWithDate(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                            @PathVariable long itemId,
                                                            @RequestBody @Valid CommentDto text) {
        return service.createComment(userId, itemId, text);
    }
}
