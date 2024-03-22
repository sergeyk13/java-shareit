package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.utils.MyPageRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDtoResponse bookingCreate(long userId, BookingDto bookingDto) {

        checkTime(bookingDto);
        Booking booking = BookingMapper.INSTANCE.dtoToModel(bookingDto);
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Item> optionalItem = itemRepository.findById(bookingDto.getItemId());

        if (optionalUser.isPresent()) {
            booking.setBooker(optionalUser.get());
        } else {
            log.error("User ID:{} not found", userId);
            throw new NotFoundException(String.format("User ID:%d not found", userId));
        }

        if (optionalItem.isPresent()) {
            booking.setItem(optionalItem.get());
            if (booking.getItem().getOwnerId() == userId) {
                log.error("Booker is owner");
                throw new NotFoundException("Booker is owner");
            }
        } else {
            log.error("Item ID:{} not found", bookingDto.getItemId());
            throw new NotFoundException(String.format("Item ID:%d not found", bookingDto.getItemId()));
        }

        if (booking.getItem().getAvailable()) {
            checkValidation(booking);
            booking.setState(calculateState(booking));
            bookingRepository.save(booking);
            log.info("Save booking: {}", booking);
        } else {
            log.error("Item isn't available");
            throw new ValidationException("Item isn't available");
        }

        return BookingMapper.INSTANCE.modelToResponse(booking);
    }

    @Transactional
    @Override
    public BookingDtoResponse bookingApprove(long userId, long bookingId, boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if (booking.get().getItem().getOwnerId() == userId) {
                if (approved) {
                    if (!booking.get().getStatus().equals(BookingState.APPROVED)) {
                        booking.get().setStatus(BookingState.APPROVED);
                        booking.get().setState(calculateState(booking.get()));
                        bookingRepository.save(booking.get());
                        log.info("Set approve");
                    } else {
                        log.error("Status is already APPROVED");
                        throw new ValidationException("Status is already APPROVED");
                    }
                } else {
                    booking.get().setStatus(BookingState.REJECTED);
                    bookingRepository.save(booking.get());
                }
            } else {
                log.error("User isn't owner");
                throw new NotFoundException("User isn't owner");
            }
        } else {
            log.error("Booking ID:{} not found", bookingId);
            throw new NotFoundException(String.format("Booking ID:%d not found", bookingId));
        }

        return BookingMapper.INSTANCE.modelToResponse(booking.get());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBookingById(long userId, long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if ((booking.get().getItem().getOwnerId() == userId) ||
                    (booking.get().getBooker().getId() == userId)) {
                booking.get().setState(calculateState(booking.get()));
                log.info("return booking: {}", bookingId);
                return BookingMapper.INSTANCE.modelToResponse(booking.get());
            } else {
                log.error("User isn't owner or booker");
                throw new NotFoundException("User isn't owner or booker");
            }
        } else {
            log.error("Booking ID:{} not found", bookingId);
            throw new NotFoundException(String.format("Booking ID:%d not found", bookingId));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingByState(long userId, BookingState state,
                                                                      int from, int size) {
        checkUser(userId);
        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = MyPageRequest.of(from, size, sortByStart);
        Page<Booking> bookings = bookingRepository.findBookingsByBookerIdOrderByStartDesc(pageable, userId);
        log.info("Return bookings by state: {}", state);
        return getListSortedByState(bookings, state, from, size);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingForOwnerByState(long userId, BookingState state,
                                                                              int from, int size) {
        checkUser(userId);
        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = MyPageRequest.of(from, size, sortByStart);

        Page<Booking> bookings = bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(pageable, userId);
        log.info("Return bookings by state: {}", state);
        return getListSortedByState(bookings, state, from, size);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getAll(int from, int size) {
        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(from, size, sortByStart);
        Page<Booking> bookingPage = bookingRepository.findAll(page);
        log.info("return all booking from:{} size:{}", from, size);
        return bookingPage.stream()
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toList());

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

    private List<BookingDtoResponse> mapListToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toList());
    }

    private List<BookingDtoResponse> mapListToDtoByStatus(List<Booking> bookings, BookingState state) {
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals(state))
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toList());
    }

    private List<BookingDtoResponse> mapListToDtoByState(List<Booking> bookings, BookingState state) {
        return bookings.stream()
                .filter(booking -> booking.getState().equals(state))
                .map(BookingMapper.INSTANCE::modelToResponse)
                .collect(Collectors.toList());
    }

    private List<BookingDtoResponse> getListSortedByState(Page<Booking> bookingsPage,
                                                                          BookingState state,
                                                                          int from,
                                                                          int size) {
        int elementOnPage = from % size;
        List<Booking> bookings = bookingsPage.stream()
                .skip(elementOnPage)
                .limit(size)
                .collect(Collectors.toList());
        bookings.forEach(booking -> booking.setState(calculateState(booking)));

        List<BookingDtoResponse> sortedBookings;
        switch (state) {
            case ALL:
                sortedBookings = mapListToDto(bookings);
                break;
            case PAST:
            case FUTURE:
            case CURRENT:
                sortedBookings = mapListToDtoByState(bookings, state);
                break;
            case WAITING:
            case APPROVED:
            case REJECTED:
                sortedBookings = mapListToDtoByStatus(bookings, state);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        return sortedBookings;
    }


    private BookingState calculateState(Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            return BookingState.PAST;
        } else if (booking.getStart().isAfter(LocalDateTime.now())) {
            return BookingState.FUTURE;
        } else return BookingState.CURRENT;
    }
}
