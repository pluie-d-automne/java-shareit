package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandlingControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import static org.hamcrest.Matchers.is;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandlingControllerAdvice.class)
                .build();

        userDto = new UserDto(
                1L,
                "Test User",
                "test@email.com");
    }

    @Test
    void create() throws Exception {
        Mockito.when(userService.create(Mockito.any()))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void findOne() throws Exception {
        Mockito.when(userService.findOne(Mockito.anyLong()))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void findOneNotFound() throws Exception {
        Mockito.when(userService.findOne(Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete() throws Exception {
        Mockito.doNothing().when(userService).delete(Mockito.anyLong());

        mvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteNotFound() throws Exception {
        Mockito.doThrow(NotFoundException.class).when(userService).delete(Mockito.anyLong());

        mvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateNotFound() throws Exception {
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
