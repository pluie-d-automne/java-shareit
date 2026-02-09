package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findOne(Long itemId);

    List<ItemDto> findItemsByOwner(Long userId);

    List<ItemDto> searchItemsByText(String text);
}
