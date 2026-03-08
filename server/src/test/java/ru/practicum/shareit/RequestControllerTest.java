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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Some request description", LocalDateTime.now());

    private ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto(
            1L, "Some request description", LocalDateTime.now(), null);

    @Test
    void createRequest() throws Exception {
        Mockito.when(itemRequestService.create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter))));
    }

    @Test
    void findRequestsByUser() throws Exception {
        Mockito.when(itemRequestService.findRequestsByUser(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(formatter))));
    }

    @Test
    void findRequestsOtherUsers() throws Exception {
        Mockito.when(itemRequestService.findRequestsOtherUsers(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(formatter))));
    }

    @Test
    void findRequestById() throws Exception {
        Mockito.when(itemRequestService.findOne(Mockito.anyLong()))
                .thenReturn(itemRequestWithItemsDto);

        mvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItemsDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemsDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestWithItemsDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items", is(itemRequestWithItemsDto.getItems())));
    }
}
