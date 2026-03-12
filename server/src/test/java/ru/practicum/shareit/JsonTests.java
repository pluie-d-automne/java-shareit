package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JsonTests {
    private final JacksonTester<ItemRequestDto> jsonRequestDto;
    private final JacksonTester<ItemRequestWithItemsDto> jsonItemRequestWithItemsDto;
    private final JacksonTester<CommentDto> jsonCommentDto;
    private final JacksonTester<BookingDto> jsonBookingDto;
    private final JacksonTester<BookingPostDto> jsonBookingPostDto;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testJsonRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto requestDto = new ItemRequestDto(
                1L,
                "Test request",
                created);

        JsonContent<ItemRequestDto> result = jsonRequestDto.write(requestDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test request");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(formatter));
    }

    @Test
    void testJsonItemRequestWithItemsDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestWithItemsDto requestDto = new ItemRequestWithItemsDto(
                1L,
                "Test request",
                created,
                null);

        JsonContent<ItemRequestWithItemsDto> result = jsonItemRequestWithItemsDto.write(requestDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test request");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(formatter));
        Assertions.assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(null);
    }

    @Test
    void testJsonCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "Some comment", "Test author", created);

        JsonContent<CommentDto> result = jsonCommentDto.write(commentDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Some comment");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(formatter));
        Assertions.assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Test author");
    }

    @Test
    void testJsonBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        ItemDto itemDto = new ItemDto(1L, "Some item", "Some desc", true, 2L);
        User booker = new User(1L, "Some user", "user@email.com");
        BookingDto bookingDto = new BookingDto(1L, itemDto, booker, BookingStatus.WAITING, start, end);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(2);
        Assertions.assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Some item");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Some user");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
    }

    @Test
    void testJsonBookingPostDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingPostDto bookingDto = new BookingPostDto(1L, start, end);

        JsonContent<BookingPostDto> result = jsonBookingPostDto.write(bookingDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
    }
}
