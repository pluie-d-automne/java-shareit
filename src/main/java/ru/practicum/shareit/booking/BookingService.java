package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto create(Long userId, BookingPostDto bookingPostDto);

    BookingDto findOne(Long userId, Long bookingId);

    BookingDto approve(Long userId, Long bookingId, boolean approved);

    List<BookingDto> findByBookerId(Long bookerId, Optional<BookingState> state);

    List<BookingDto> findByOwnerId(Long ownerId, Optional<BookingState> state);

    Booking findBookingById(Long bookingId);
}
