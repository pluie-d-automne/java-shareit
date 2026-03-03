package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;

    public BookingDto toBookingDto(Booking booking) {
        log.info("Convert booking {} to BookingDto.", booking);
            return new BookingDto(
                    booking.getId(),
                    itemMapper.toItemDto(booking.getItem()),
                    booking.getBooker(),
                    booking.getStatus(),
                    booking.getStart(),
                    booking.getEnd()
            );
    }
}

