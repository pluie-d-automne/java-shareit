package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findOne(Long itemId);

    Collection<ItemDto> findItemsByOwner(Long userId);

    Collection<ItemDto> itemTextSearch(String text);
}
