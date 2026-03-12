package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDates;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userService.findUserById(userId);
        Item item = new Item(itemDto.getId(),
                user,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId());
        item = itemRepository.save(item);
        log.info("Saved item {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = findItemById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only owner can update an item.");
        }

        User user = item.getOwner();
        String newName = itemDto.getName();
        String newDescription = itemDto.getDescription();
        Boolean newAvailable = itemDto.getAvailable();

        if (newName != null) {
            item.setName(newName);
        }

        if (newDescription != null) {
            item.setDescription(newDescription);
        }

        if (newAvailable != null) {
            item.setAvailable(newAvailable);
        }

        log.info("Update item {} with id={} by user {}", itemDto, itemId, user);
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemOwnerDto findOne(Long userId, Long itemId) {
        log.info("Look for item by id={}",itemId);
        Item item = findItemById(itemId);
        BookingDates lastBooking = null;
        BookingDates nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.getLastBookingsByItemId(item.getId());
            nextBooking = bookingRepository.getNextBookingsByItemId(item.getId());
        }
        List<Comment> comments = commentRepository.findByItemId(itemId);
        log.info("Comments found: {}",comments);
        return itemMapper.toItemOwnerDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemOwnerDto> findItemsByOwner(Long userId) {
        log.info("Look for items of userId={}",userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(item -> itemMapper.toItemOwnerDto(item,
                        bookingRepository.getLastBookingsByItemId(item.getId()),
                        bookingRepository.getNextBookingsByItemId(item.getId()),
                        commentRepository.findByItemId(item.getId())))
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        log.info("Look for items by text={}",text);
        List<Item> items = itemRepository.findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(text, text);
        return items.stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public Item findItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new NotFoundException("Item with id=" + itemId + " was not found.");
        } else {
            Item itemFound = item.get();
            log.info("Found item {} by item_id {}", itemFound, itemId);
            return itemFound;
        }
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto comment) {
        log.info("Try to add new comment {} by autorId={} to itemId={}", comment, authorId, itemId);
        LocalDateTime now = LocalDateTime.now();
        User author = userService.findUserById(authorId);
        Item item = findItemById(itemId);
        List<Booking> bookings = bookingRepository.getByBookerIdAndItemId(authorId, itemId);

        if (bookings.isEmpty()) {
            throw new BadRequest("User " + authorId + " has never booked item " + itemId);
        }

        LocalDateTime start = bookings.stream()
                .map(Booking::getStart)
                .min(LocalDateTime::compareTo)
                .get();

        if (start.isAfter(now)) {
            throw new BadRequest("User " + authorId + " booking of item " + itemId + "has not started. Start: "
                    + start + ". Now: " + now);
        }

        Comment newComment = new Comment(comment.getId(), comment.getText(), item, author, LocalDateTime.now());
        newComment = commentRepository.save(newComment);
        log.info("Add new comment: {}", newComment);
        return itemMapper.toCommentDto(newComment);
    }
}
