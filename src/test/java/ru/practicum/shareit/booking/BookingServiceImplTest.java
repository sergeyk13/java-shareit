package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.FactoryEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    long ownerId = 11L;
    User booker = FactoryEntity.creatRandomUser();
    Item item = FactoryEntity.createRandomItem(ownerId);
    long bookerId = booker.getId();
    BookingDto bookingDto = FactoryEntity.createRandomBookingDto(item);
    User owner = FactoryEntity.creatRandomUser();
    Booking booking = FactoryEntity.createRandomBooking(item, booker);
    long bookingId = booking.getId();
    boolean approved = false;

    @BeforeEach
    void setUp() {
        booking.setStatus(BookingState.WAITING);
    }

    @Test
    void bookingCreateValidBookingSuccess() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<BookingDtoResponse> response = bookingService.bookingCreate(booker.getId(), bookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void bookingCreateUserNotFoundThrowsNotFoundException() {

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.bookingCreate(bookerId, bookingDto));
    }

    @Test
    void bookingCreateItemNotFoundThrowsNotFoundException() {

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.bookingCreate(bookerId, bookingDto));
    }

    @Test
    void bookingApproveValidBookingApprovedSuccess() {

        boolean approved = true;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseEntity<BookingDtoResponse> response = bookingService.bookingApprove(ownerId, bookingId, approved);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BookingState.APPROVED, booking.getStatus());

    }

    @Test
    void bookingApproveValidBookingRejectedSuccess() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseEntity<BookingDtoResponse> response = bookingService.bookingApprove(ownerId, bookingId, approved);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BookingState.REJECTED, booking.getStatus());
    }

    @Test
    void bookingApproveUserNotIsOwnerThrowsNotFoundException() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.bookingApprove(ownerId + 1, bookingId, approved));
    }

    @Test
    void bookingApprove_BookingNotFound_ThrowsNotFoundException() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.bookingApprove(ownerId, bookingId, approved));
    }

}
