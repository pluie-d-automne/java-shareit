package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 order by b.start desc")
    List<Booking> getAllBookingsByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND CURRENT_TIMESTAMP() BETWEEN b.start AND b.end order by b.start desc")
    List<Booking> getCurrentBookingsByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> getPastBookingsByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> getFutureBookingsByBookerId(Long userId);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.owner.id = ?1 order by b.start desc")
    List<Booking> getAllBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.owner.id = ?1 AND CURRENT_TIMESTAMP() BETWEEN b.start AND b.end order by b.start desc")
    List<Booking> getCurrentBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.owner.id = ?1 AND b.end < CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> getPastBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.owner.id = ?1 AND b.start > CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> getFutureBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.owner.id = ?1 AND b.status = ?2")
    List<Booking> getByOwnerIdAndStatus(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end < CURRENT_TIMESTAMP() order by b.end desc LIMIT 1")
    BookingDates getLastBookingsByItemId(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end > CURRENT_TIMESTAMP() order by b.start asc LIMIT 1")
    BookingDates getNextBookingsByItemId(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.item.id = ?2")
    List<Booking> getByBookerIdAndItemId(Long bookerId, Long itemId);
}
