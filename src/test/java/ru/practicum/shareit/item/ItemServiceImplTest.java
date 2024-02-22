package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapperInt;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.FactoryEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private final Item testItem = new Item(1L, 1L, "nameItem", "itemDesc", true, null);
    private final User testUser = new User(1L, "Name", "email@mail.com");
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void saveItemValidUserAndItemDtoShouldReturnSavedItemDto() {

        long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "nameItem", "itemDesc", true, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapperInt.INSTANCE.dtoToModel(itemDto, userId));

        ItemDto savedItemDto = itemService.saveItem(userId, itemDto);

        assertNotNull(savedItemDto);
    }

    @Test
    void saveItemInvalidUserShouldThrowNotFoundException() {

        long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "nameItem", "itemDesc", true, null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveItem(userId, itemDto));
    }

    @Test
    void prepareUpdatingValidUserAndItemIdShouldReturnUpdatedItem() {

        long userId = 1L;
        long itemId = 1L;
        ItemUpdatingRequest itemUpdatingRequest = new ItemUpdatingRequest(null, "NewDesc", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        Item updatedItem = itemService.prepareUpdating(userId, itemId, itemUpdatingRequest);

        assertNotNull(updatedItem);
        assertEquals(itemUpdatingRequest.getDescription(), updatedItem.getDescription());
    }

    @Test
    void prepareUpdatingInvalidUserShouldThrowNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        ItemUpdatingRequest itemUpdatingRequest = new ItemUpdatingRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.prepareUpdating(userId, itemId, itemUpdatingRequest));
    }

    @Test
    void prepareUpdatingInvalidItemShouldThrowNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        ItemUpdatingRequest itemUpdatingRequest = new ItemUpdatingRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.prepareUpdating(userId, itemId, itemUpdatingRequest));
    }

    @Test
    void getItemByIdWithDateValidItemAndUserShouldReturnItemResponseDto() {

        long itemId = 1L;
        long userId = 1L;
        List<Booking> bookingList = FactoryEntity.generateRandomBookingList(3, testItem, testUser);
        Page<Booking> bookingsPage = new PageImpl<>(bookingList, PageRequest.of(0, bookingList.size()), bookingList.size());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.findCommentByItem_IdOrderByCreated(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findBookingsByItemIdOrderByStartDesc(any(PageRequest.class), anyLong()))
                .thenReturn(bookingsPage);

        ItemResponseDto itemResponseDto = itemService.getItemByIdWithDate(itemId, userId);

        assertNotNull(itemResponseDto);
        assertEquals(itemId, itemResponseDto.getId());
    }

    @Test
    void getItemByIdWithDateInvalidItemShouldThrowNotFoundException() {

        Long itemId = 1L;
        long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> itemService.getItemByIdWithDate(itemId, userId));
    }
}
