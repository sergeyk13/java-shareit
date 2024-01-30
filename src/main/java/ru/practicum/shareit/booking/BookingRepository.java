package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Set<Booking> findBookingsByBooker_IdAndStateOrderByStart(Long bookerId, BookingState state);

    Set<Booking> findBookingsByBooker_IdOrderByStart(Long bookerId);

    Set<Booking> findBookingsByBooker_IdAndStatusOrderByStart(Long bookersID, BookingState state);

    Set<Booking> findBookingsByItem_OwnerIdAndStateOrderByStartDesc(Long bookerId, BookingState state);

    Set<Booking> findBookingsByItem_OwnerIdAndStatusOrderByStart(Long bookersID, BookingState state);

    Set<Booking> findBookingsByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findBookingsByItem_IdOrderByStart(Long itemId);

    Set<Booking> findBookingsByBooker_IdAndItem_Id(Long bookerId, Long itemId);
}
