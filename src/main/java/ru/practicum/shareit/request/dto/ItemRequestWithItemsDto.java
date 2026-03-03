package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestWithItemsDto {

    private Long id;

    @NotBlank(message = "Description should not be blank.")
    private String description;

    private LocalDateTime created;

    private List<ItemForRequestDto> items;
}
