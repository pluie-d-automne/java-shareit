package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;

import java.util.List;
import java.util.Optional;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingPostDto bookingPostDto) {
        return bookingService.create(userId, bookingPostDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam(name = "approved") boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto finedOne(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.findOne(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findByBookerId(@RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
                                     @PathVariable Optional<BookingState> state) {
        return bookingService.findByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                           @PathVariable Optional<BookingState> state) {
        return bookingService.findByOwnerId(ownerId, state);
    }
}
