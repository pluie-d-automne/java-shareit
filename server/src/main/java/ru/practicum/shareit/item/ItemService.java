package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemOwnerDto findOne(Long userId, Long itemId);

    List<ItemOwnerDto> findItemsByOwner(Long userId);

    List<ItemDto> searchItemsByText(String text);

    Item findItemById(Long itemId);

    CommentDto addComment(Long authorId, Long itemId, CommentDto comment);
}
