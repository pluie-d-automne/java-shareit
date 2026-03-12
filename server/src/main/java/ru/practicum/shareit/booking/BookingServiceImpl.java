package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto create(Long userId, BookingPostDto bookingPostDto) {
        log.info("User with id={} wants to add booking: {}", userId, bookingPostDto);
        Long itemId = bookingPostDto.getItemId();
        User booker = userService.findUserById(userId);
        Item item = itemService.findItemById(itemId);
        LocalDateTime start = bookingPostDto.getStart();
        LocalDateTime end = bookingPostDto.getEnd();

        if (!start.isBefore(end)) {
            throw new BadRequest("Start " + start + " should be before end " + end);
        }

        if (!item.getAvailable()) {
            throw new BadRequest("Item with id=" + itemId + " is not available for booking.");
        }

        log.info("Prepare new booking by userId {} for itemId {} for the period {}-{}.", userId, itemId, start, end);
        Booking booking = new Booking(null, item, booker, BookingStatus.WAITING, start, end);
        booking = bookingRepository.save(booking);
        log.info("Created new booking: {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findOne(Long userId, Long bookingId) {
        Booking booking = findBookingById(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new UnauthorizedException("Only owner or booker can view booking details.");
        }
        log.info("The booking was found {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        log.info("User with id={} wants to approve={} booking with id={}", userId, approved, bookingId);
        Booking booking = findBookingById(bookingId);
        User owner = booking.getItem().getOwner();

        if (!owner.getId().equals(userId)) {
            throw new UnauthorizedException("Only owner can approve or reject a booking.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("Approving booking {}", booking);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Rejecting booking {}", booking);
        }

        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findByBookerId(Long bookerId, Optional<BookingState> state) {
        BookingState bookingState = state.orElse(BookingState.ALL);

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.getAllBookingsByBookerId(bookerId);
            case CURRENT -> bookingRepository.getCurrentBookingsByBookerId(bookerId);
            case PAST -> bookingRepository.getPastBookingsByBookerId(bookerId);
            case FUTURE -> bookingRepository.getFutureBookingsByBookerId(bookerId);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findByOwnerId(Long ownerId, Optional<BookingState> state) {
        BookingState bookingState = state.orElse(BookingState.ALL);

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.getAllBookingsByOwnerId(ownerId);
            case CURRENT -> bookingRepository.getCurrentBookingsByOwnerId(ownerId);
            case PAST -> bookingRepository.getPastBookingsByOwnerId(ownerId);
            case FUTURE -> bookingRepository.getFutureBookingsByOwnerId(ownerId);
            case WAITING -> bookingRepository.getByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.getByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
        };

        if (bookings.isEmpty()) {
            throw new NotFoundException("Owner with id=" + ownerId + " does not have booked " + bookingState + " items.");
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public Booking findBookingById(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new NotFoundException("Booking with id=" + bookingId + " does not exists");
        } else {
            Booking bookingFound = booking.get();
            log.info("Found booking {} by booking_id {}", bookingFound, bookingId);
            return bookingFound;
        }
    }
}
