package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findBookingsByItemIdOrderByStartDesc(Long itemId);

    Set<Booking> findBookingsByBookerIdAndItemId(Long bookerId, Long itemId);
}
