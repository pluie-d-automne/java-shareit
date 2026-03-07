package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findRequestsByUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.findRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findRequestsOtherUsers(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.findRequestsOtherUsers(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestWithItemsDto findRequestById(@PathVariable(name = "id") long itemRequestId) {
        return itemRequestService.findOne(itemRequestId);
    }
}
