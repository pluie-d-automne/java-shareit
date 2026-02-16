package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Is not valid email.")
    @NotBlank(groups = Marker.OnCreate.class, message = "Email can not be blank.")
    private String email;
}
