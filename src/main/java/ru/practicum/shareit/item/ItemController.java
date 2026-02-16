package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable(name = "id") long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemOwnerDto> findItemsByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.findItemsByOwner(userId);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto findOne(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(name = "id") long itemId) {
        return itemService.findOne(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader(value = "X-Sharer-User-Id") Long authorId,
                                 @PathVariable(name = "id") Long itemId,
                                 @RequestBody Comment comment) {
        return itemService.addComment(authorId, itemId, comment);
    }
}
