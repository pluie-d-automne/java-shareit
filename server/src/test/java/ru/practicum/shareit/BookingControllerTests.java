package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ItemDto item = new ItemDto(1L, "Test item", "Test desc", true, 2L);

    private User booker = new User(1L, "Test name", "test@email.com");

    private BookingDto bookingDto = new BookingDto(
            1L,
            item,
            booker,
            BookingStatus.WAITING,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2));

    private BookingPostDto bookingPostDto = new BookingPostDto(1L,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

    @Test
    void createBooking() throws Exception {
        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any(BookingPostDto.class)))
                .thenReturn(bookingDto);

        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void approveBooking() throws Exception {
        Mockito.when(bookingService.approve(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void findOne() throws Exception {
        Mockito.when(bookingService.findOne(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void findByBookerId() throws Exception {
        Mockito.when(bookingService.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(MockMvcRequestBuilders.get("/bookings?state=all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void findByOwnerId() throws Exception {
        Mockito.when(bookingService.findByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }
}
