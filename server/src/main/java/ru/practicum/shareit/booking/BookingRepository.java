package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findBookingsByBookerIdOrderByStartDesc(Pageable page, Long bookerId);

    Page<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Pageable pageable, Long ownerId);

    Page<Booking> findBookingsByItemIdOrderByStartDesc(Pageable pageable, Long itemId);

    Set<Booking> findBookingsByBookerIdAndItemId(Long bookerId, Long itemId);
}
