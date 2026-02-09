package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    ItemDto create(User user, ItemDto itemDto);

    ItemDto update(User user, Long itemId, ItemDto itemDto);

    ItemDto findOne(Long itemId);

    List<ItemDto> findItemsByOwner(User user);

    List<ItemDto> searchItemsByText(String text);
}
