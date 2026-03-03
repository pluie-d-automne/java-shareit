package ru.practicum.shareit.booking.dto;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BookingPostDto {
    private Long itemId;

    @Future(message = "Booking should start in the future.")
    private LocalDateTime start;

    @Future(message = "Booking should start in the future.")
    private LocalDateTime end;
}
