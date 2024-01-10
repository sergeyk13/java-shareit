package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();

    private long id = 1;

    @Override
    public ItemDto itemCreate(ItemDto itemDto, long userId) {
        Item item = ItemMapper.itemDtoToItem(itemDto);
        item.setOwnerId(userId);
        item.setId(getId());
        items.put(item.getId(), item);
        return ItemMapper.itemToItemDto(items.get(item.getId()));
    }

    @Override
    public ItemDto getItem(long itemId) {
        isItemExist(itemId);
        return ItemMapper.itemToItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> findItemByOwnerId(long userId) {
        List<ItemDto> itemList = new ArrayList<>();
        items.forEach((key, value) -> {
            if (value.getOwnerId() == userId) {
                itemList.add(ItemMapper.itemToItemDto(value));
            }
        });
        return Collections.unmodifiableList(itemList);
    }


    @Override
    public List<ItemDto> getAll() {
        List<ItemDto> itemList = new ArrayList<>();
        items.forEach((key, value) -> itemList.add(ItemMapper.itemToItemDto(value)));
        return Collections.unmodifiableList(itemList);
    }

    @Override
    public ItemDto addUpdatingItem(long itemId, Item item) {
        items.put(itemId, item);
        return ItemMapper.itemToItemDto(items.get(item.getId()));
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest) {
        isItemExist(itemId);
        checkOwner(userId, itemId);
        Item item = items.get(itemId);
        Optional<String> name = Optional.ofNullable(itemUpdatingRequest.getName());
        Optional<String> description = Optional.ofNullable(itemUpdatingRequest.getDescription());
        Optional<Boolean> available = Optional.ofNullable(itemUpdatingRequest.getAvailable());

        if (name.isEmpty() && description.isEmpty() && available.isEmpty()) {
            throw new ValidationException("At least one field for update should be provided.");
        }
        name.ifPresent(n -> {
            if (!n.isBlank()) {
                item.setName(n);
            }
        });

        description.ifPresent(d -> {
            if (!d.isBlank()) {
                item.setDescription(d);
            }
        });

        available.ifPresent(item::setAvailable);

        return item;
    }

    @Override
    public boolean removeItem(long itemId) {
        return items.remove(itemId) != null;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        List<ItemDto> itemList = new ArrayList<>();
        String searchTextLower = searchText.toLowerCase();

        items.forEach((key, value) -> {
            if (value.getAvailable() && (value.getName().toLowerCase().contains(searchTextLower) || value.getDescription().toLowerCase().contains(searchTextLower))) {
                itemList.add(ItemMapper.itemToItemDto(value));
            }
        });

        return Collections.unmodifiableList(itemList);
    }

    private void isItemExist(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("User should be owner");
        }
    }

    private void checkOwner(long userId, long itemId) {
        if (!(items.get(itemId).getOwnerId() == userId)) {
            throw new NotFoundException(String.format("User: %d isn't the owner by item: %d", userId, itemId));

        }
    }

    private long getId() {
        return this.id++;
    }
}
