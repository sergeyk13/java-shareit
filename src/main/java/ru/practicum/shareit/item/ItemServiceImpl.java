package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto itemCreate(long userId, ItemDto itemDto) {
        log.info("Create Item: {}", itemDto);
        userRepository.getUser(userId);
        return itemRepository.itemCreate(itemDto, userId);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        userRepository.getUser(userId);
        log.info("get Item by Id: {}", itemId);
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.getAll();
    }

    @Override
    public List<ItemDto> getAllByUserId(long userId) {
        userRepository.getUser(userId);
        log.info("Get all items by user: {}", userId);
        return itemRepository.findItemByOwnerId(userId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        log.info("Update Item: {}", item);
        return itemRepository.addUpdatingItem(itemId, item);
    }

    @Override
    public void removeItem(long userId, long itemId) {
        userRepository.getUser(userId);
        log.info("remove Item: {}", itemId);
        itemRepository.removeItem(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        log.info("Search items contain: " + searchText);
        if (searchText.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchItems(searchText);
        }
    }

    public Item prepareUpdating(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest) {
        log.info("Preparing Item: {}", itemId);
        return itemRepository.updateItem(userId, itemId, itemUpdatingRequest);
    }
}
