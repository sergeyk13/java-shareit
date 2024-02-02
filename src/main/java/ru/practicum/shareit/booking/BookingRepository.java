package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Set<Booking> findBookingsByBookerIdOrderByStart(Long bookerId);

    Set<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findBookingsByItemIdOrderByStart(Long itemId);

    Set<Booking> findBookingsByBookerIdAndItem_Id(Long bookerId, Long itemId);
}
