package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ItemDto itemDto = new ItemDto(
            1L, "Test item", "Test desc", true, 3L);

    private ItemOwnerDto itemOwnerDto = new ItemOwnerDto(1L, "Test item", "Test desc", true,
            3L, null, null, null);

    private CommentDto commentDto = new CommentDto(1L, "Some comment", "Test author", LocalDateTime.now());

    @Test
    void createItem() throws Exception {
        Mockito.when(itemService.create(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void createItemNotFound() throws Exception {
        Mockito.when(itemService.create(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(itemService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void findItemsByOwner() throws Exception {
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong()))
                .thenReturn(List.of(itemOwnerDto));

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$[0].requestId", is(itemOwnerDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].available", is(itemOwnerDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemOwnerDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemOwnerDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemOwnerDto.getComments())));
    }

    @Test
    void findOne() throws Exception {
        Mockito.when(itemService.findOne(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemOwnerDto);

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemOwnerDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemOwnerDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemOwnerDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemOwnerDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemOwnerDto.getComments())));
    }

    @Test
    void searchItemsByText() throws Exception {
        Mockito.when(itemService.searchItemsByText(Mockito.anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=sometext")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void addComment() throws Exception {
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(formatter))));
    }
}

