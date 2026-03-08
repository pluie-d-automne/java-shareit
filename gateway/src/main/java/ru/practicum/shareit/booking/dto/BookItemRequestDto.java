package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;

    @FutureOrPresent(message = "Booking should start in the future or now.")
    private LocalDateTime start;

    @Future(message = "Booking should end in the future.")
    private LocalDateTime end;
}