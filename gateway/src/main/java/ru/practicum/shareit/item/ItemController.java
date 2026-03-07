package ru.practicum.shareit.item;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable(name = "id") long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemClient.findItemsByOwner(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(name = "id") long itemId) {
        return itemClient.findOne(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text) {
        return itemClient.searchItemsByText(text);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") Long authorId,
                                 @PathVariable(name = "id") Long itemId,
                                 @RequestBody CommentDto comment) {
        return itemClient.addComment(authorId, itemId, comment);
    }
}
