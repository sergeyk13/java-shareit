package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.model.ItemUpdatingRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
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
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isPresent()) {
            try {
                Item savedItem = ItemMapperInt.INSTANCE.dtoToModel(itemDto, userId);
                if (itemDto.getRequestId() != null) {
                    requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                            new NotFoundException(
                                    String.format("Request with ID:%d not found", itemDto.getRequestId())));
                }
                log.info("Create Item: {}", savedItem);
                itemRepository.save(savedItem);
                return ItemMapperInt.INSTANCE.modelToDto(savedItem);
            } catch (DataIntegrityViolationException e) {
                log.error("Error save Item: {}, owner id isn't found", itemDto);
                throw new NotFoundException(String.format("Error save Item owner id: %d isn't found", userId));
            }
        } else throw new NotFoundException(String.format("User with ID:%d not found", userId));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItem(long userId, long itemId) {
        log.info("getting Item by Id: {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item with Id:%d not found", itemId)));
        return ItemMapperInt.INSTANCE.modelToDto(item);
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
        return ItemMapperInt.INSTANCE.modelToDto(item);

    }

    @Transactional
    @Override
    public void removeItem(long userId, long itemId) {
        if (!checkOwner(userId, itemId)) {
            throw new ConflictException("User does not have access to remove this item");
        }
        log.info("remove Item: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String searchText, int from, int size) {

        if (searchText.isBlank()) {
            return new ArrayList<>();
        } else {
            log.info("Search items contain: " + searchText);
            List<Item> items = itemRepository.searchItems(searchText.toLowerCase());
            return items.stream()
                    .map(ItemMapperInt.INSTANCE::modelToDto)
                    .collect(Collectors.toList());
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

                Pageable page = PageRequest.of(0, 30, Sort.by("start").descending());
                Page<Booking> bookingsPages = bookingRepository.findBookingsByItemIdOrderByStartDesc(page, itemId);
                List<Booking> bookings = bookingsPages.stream()
                        .collect(Collectors.toList());

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

                return ItemMapperInt.INSTANCE.mapToItemResponseDto(itemOptional.get(),
                        BookingMapper.INSTANCE.modelToBookingByItem(lastBooking),
                        BookingMapper.INSTANCE.modelToBookingByItem(nextBooking),
                        comments.stream()
                                .map(ItemMapperInt.INSTANCE::modelCommentToDto)
                                .collect(Collectors.toList()));
            } else return ItemMapperInt.INSTANCE.mapToItemResponseDto(itemOptional.get(), null, null,
                    comments.stream()
                            .map(ItemMapperInt.INSTANCE::modelCommentToDto)
                            .collect(Collectors.toList()));
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    @Transactional
    @Override
    public CommentDtoResponse createComment(long userId, long itemId, @Valid CommentDto text) {
        Set<Booking> bookingSet = bookingRepository.findBookingsByBookerIdAndItemId(userId, itemId);
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

        return ItemMapperInt.INSTANCE.modelCommentToDto(comment);
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
