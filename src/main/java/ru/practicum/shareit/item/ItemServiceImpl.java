package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    private final UserService userService;

    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Create item {} by user {}", itemDto, userId);
        userService.findOne(userId);
        return itemDao.create(userId, itemDto);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Update item {} with id={} by user {}", itemDto, itemId, userId);
        return itemDao.update(userId, itemId, itemDto);
    }

    public ItemDto findOne(Long itemId) {
        log.info("Look for item by id={}",itemId);
        return itemDao.findOne(itemId);
    }

    public Collection<ItemDto> findItemsByOwner(Long userId) {
        log.info("Look for items of userId={}",userId);
        return itemDao.findItemsByOwner(userId);
    }

    public Collection<ItemDto> itemTextSearch(String text) {
        log.info("Look for items by text={}",text);
        return itemDao.itemTextSearch(text);
    }
}
