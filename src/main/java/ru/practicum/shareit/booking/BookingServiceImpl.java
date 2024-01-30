package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ResponseEntity<BookingDtoResponse> bookingCreate(long userId, BookingDto bookingDto) {

        checkTime(bookingDto);
        Booking booking = BookingMapper.INSTANCE.dtoToModel(bookingDto);
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Item> optionalItem = itemRepository.findById(bookingDto.getItemId());

        if (optionalUser.isPresent()) {
            booking.setBooker(optionalUser.get());
        } else throw new NotFoundException(String.format("User ID:%d not found", userId));


        if (optionalItem.isPresent()) {
            booking.setItem(optionalItem.get());
            if (booking.getItem().getOwnerId() == userId) {
                throw new NotFoundException("Booker is owner");
            }
        } else throw new NotFoundException(String.format("Item ID:%d not found", bookingDto.getItemId()));

        if (booking.getItem().getAvailable()) {
            checkValidation(booking);
            booking.setState(calculateState(booking));
            bookingRepository.save(booking);
        } else throw new ValidationException("Item isn't available");

        return new ResponseEntity<>(BookingMapper.INSTANCE.modelToResponse(booking),
                HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<BookingDtoResponse> bookingApprove(long userId, long bookingId, boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if (booking.get().getItem().getOwnerId() == userId) {
                if (approved) {
                    if (!booking.get().getStatus().equals(BookingState.APPROVED)) {
                        booking.get().setStatus(BookingState.APPROVED);
                        booking.get().setState(calculateState(booking.get()));
                        bookingRepository.save(booking.get());
                    } else throw new ValidationException("Status is already APPROVED");
                } else {
                    booking.get().setStatus(BookingState.REJECTED);
                    bookingRepository.save(booking.get());
                }
            } else throw new NotFoundException("User isn't owner");
        } else throw new NotFoundException(String.format("Booking ID:%d not found", bookingId));

        return new ResponseEntity<>(BookingMapper.INSTANCE.modelToResponse(booking.get()),
                HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<BookingDtoResponse> getBookingById(long userId, long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if ((booking.get().getItem().getOwnerId() == userId) ||
                    (booking.get().getBooker().getId() == userId)) {
                booking.get().setState(calculateState(booking.get()));
                return new ResponseEntity<>(BookingMapper.INSTANCE.modelToResponse(booking.get()),
                        HttpStatus.OK);
            } else throw new NotFoundException("User isn't owner or booker");
        } else throw new NotFoundException(String.format("Booking ID:%d not found", bookingId));
    }

    @Transactional
    @Override
    public ResponseEntity<LinkedHashSet<BookingDtoResponse>> getBookingByState(long userId, BookingState state) {
        checkUser(userId);
        Set<Booking> bookings = bookingRepository.findBookingsByBooker_IdOrderByStart(userId);
        return getLinkedSetByState(bookings, state);
    }

    @Transactional
    @Override
    public ResponseEntity<LinkedHashSet<BookingDtoResponse>> getBookingForOwnerByState(long userId, BookingState state) {
        checkUser(userId);
        Set<Booking> bookings = bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(userId);
        return getLinkedSetByState(bookings, state);
    }

    private void checkValidation(@Valid Booking booking) {
    }

    private void checkTime(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("You are set wrong time");
        }
    }

    private void checkUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User ID:%d not found", userId));
        }
    }

    private LinkedHashSet<BookingDtoResponse> createLinkedSet(Set<Booking> bookings) {
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<BookingDtoResponse> createLinkedSetByStatus(Set<Booking> bookings, BookingState state) {
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals(state))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<BookingDtoResponse> createLinkedSetByState(Set<Booking> bookings, BookingState state) {
        return bookings.stream()
                .filter(booking -> booking.getState().equals(state))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ResponseEntity<LinkedHashSet<BookingDtoResponse>> getLinkedSetByState(Set<Booking> bookings,
                                                                                  BookingState state) {
        Set<Booking> bookingSet = new HashSet<>(bookings);
        bookingSet.forEach(booking -> booking.setState(calculateState(booking)));

        LinkedHashSet<BookingDtoResponse> sortedBookings;
        switch (state) {
            case ALL:
                sortedBookings = createLinkedSet(bookingSet);
                break;
            case PAST:
            case FUTURE:
            case CURRENT:
                sortedBookings = createLinkedSetByState(bookingSet, state);
                break;
            case WAITING:
            case APPROVED:
            case REJECTED:
                sortedBookings = createLinkedSetByStatus(bookingSet, state);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        return new ResponseEntity<>(sortedBookings, HttpStatus.OK);
    }


    private BookingState calculateState(Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            return BookingState.PAST;
        } else if (booking.getStart().isAfter(LocalDateTime.now())) {
            return BookingState.FUTURE;
        } else return BookingState.CURRENT;
    }
}
