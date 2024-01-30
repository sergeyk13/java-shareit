package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapperInt;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isPresent()) {
            try {
                Item savedItem = ItemMapper.itemDtoToItem(itemDto, userId);
                log.info("Create Item: {}", savedItem);
                itemRepository.save(savedItem);
                return ItemMapper.itemToItemDto(savedItem);
            } catch (DataIntegrityViolationException e) {
                log.error("Error save Item: {}, owner id isn't found", itemDto);
                throw new NotFoundException("Error save Item owner id isn't found");
            }
        } else throw new NotFoundException(String.format("User with ID:%d not found", userId));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItem(long userId, long itemId) {
        log.info("getting Item by Id: {}", itemId);
        return ItemMapper.itemToItemDto(itemRepository.findById(itemId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemResponseDto> getAllByUserId(long userId) {
        log.info("Get all items by user: {}", userId);
        List<Item> itemsId = itemRepository.findItemsByOwnerId(userId);
        return itemsId.stream()
                .map(item -> getItemByIdWithDate(item.getId(), userId))
                .sorted(Comparator.comparingLong(ItemResponseDto::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, @Valid Item item) {
        itemRepository.save(item);
        log.info("Update Item: {}", item);
        return ItemMapper.itemToItemDto(itemRepository.findById(itemId));

    }

    @Override
    public void removeItem(long userId, long itemId) {
        if (!checkOwner(userId, itemId)) {
            throw new ConflictException("User does not have access to remove this item");
        }
        log.info("remove Item: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {

        if (searchText.isBlank()) {
            return new ArrayList<>();
        } else {
            log.info("Search items contain: " + searchText);
            return ItemMapper.itemToItemDtoList(itemRepository.searchItems(searchText.toLowerCase()));
        }
    }


    public Item prepareUpdating(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest) {
        if (checkOwner(userId, itemId)) {
            Optional<Item> itemOptional = itemRepository.findById(itemId);
            Item existingItem;

            if (itemOptional.isPresent()) {
                existingItem = itemOptional.get();
            } else {
                throw new NotFoundException(String.format("User with ID: %d not found", userId));
            }

            if (itemUpdatingRequest.getName() != null) {
                existingItem.setName(itemUpdatingRequest.getName());
            }
            if (itemUpdatingRequest.getDescription() != null) {
                existingItem.setDescription(itemUpdatingRequest.getDescription());
            }
            if (itemUpdatingRequest.getAvailable() != null) {
                existingItem.setAvailable(itemUpdatingRequest.getAvailable());
            }
            return existingItem;
        } else throw new ConflictException("User does not have access to update this item");

    }

    @Override
    public ItemResponseDto getItemByIdWithDate(Long itemId, long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        Optional<User> userOptional = userRepository.findById(userId);

        Booking lastBooking;
        Booking nextBooking;

        if (itemOptional.isPresent() && userOptional.isPresent()) {
            List<Comment> comments = commentRepository.findCommentByItem_IdOrderByCreated(itemId);

            if (checkOwner(userId, itemId)) {

                List<Booking> bookings = bookingRepository.findBookingsByItem_IdOrderByStart(itemId);

                lastBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> !(booking.getStatus().equals(BookingState.REJECTED)))
                        .max(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                nextBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .filter(booking -> !(booking.getStatus().equals(BookingState.REJECTED)))
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                return ItemMapper.mapToItemResponseDto(itemOptional.get(),
                        BookingMapper.INSTANCE.modelToBookingByItem(lastBooking),
                        BookingMapper.INSTANCE.modelToBookingByItem(nextBooking),
                        comments.stream()
                                .map(ItemMapperInt.INSTANCE::modelToDto)
                                .collect(Collectors.toList()));
            } else return ItemMapper.mapToItemResponseDto(itemOptional.get(), null, null,
                    comments.stream()
                            .map(ItemMapperInt.INSTANCE::modelToDto)
                            .collect(Collectors.toList()));
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    @Override
    public ResponseEntity<CommentDtoResponse> createComment(long userId, long itemId, @Valid CommentDto text) {
        Set<Booking> bookingSet = bookingRepository.findBookingsByBooker_IdAndItem_Id(userId, itemId);
        Set<Booking> filteredBooking = bookingSet.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toSet());

        if (bookingSet.isEmpty() || filteredBooking.isEmpty()) {
            throw new ValidationException("User does not have access to create comment for this item");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setItem(existingItem);
        comment.setUser(author);
        comment.setText(text.getText());
        try {
            commentRepository.save(comment);
        } catch (ConstraintViolationException e) {
            throw new ValidationException(e.getLocalizedMessage());
        }

        return new ResponseEntity<>(ItemMapperInt.INSTANCE.modelToDto(comment),
                HttpStatus.OK);
    }


    private boolean checkOwner(long userId, long itemId) {
        try {
            User owner = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

            Item existingItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

            if (existingItem.getOwnerId() != (owner.getId())) {
                throw new AccessDeniedException("User does not have access to update this item");
            }
            return true;
        } catch (AccessDeniedException e) {
            log.error("User: {} does not have access to update this item", userId);
            return false;
        }
    }

    private LinkedHashSet<BookingDtoResponse> createLinkedSet(Set<Booking> bookings) {
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper.INSTANCE::modelToResponse
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
