package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingDates;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public ItemOwnerDto toItemOwnerDto(Item item, BookingDates lastBooking, BookingDates nextBooking, List<Comment> comments) {
        List<CommentDto> commentsDto = comments.stream().map(this::toCommentDto).toList();
        return new ItemOwnerDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                lastBooking,
                nextBooking,
                commentsDto
        );
    }
}
