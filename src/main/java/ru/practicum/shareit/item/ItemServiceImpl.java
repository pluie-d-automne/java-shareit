package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Create item {} by user {}", itemDto, userId);
        User user = userService.findUserById(userId);
        return itemRepository.create(user, itemDto);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        User user = userService.findUserById(userId);
        log.info("Update item {} with id={} by user {}", itemDto, itemId, user);
        return itemRepository.update(user, itemId, itemDto);
    }

    @Override
    public ItemDto findOne(Long itemId) {
        log.info("Look for item by id={}",itemId);
        return itemRepository.findOne(itemId);
    }

    @Override
    public List<ItemDto> findItemsByOwner(Long userId) {
        User user = userService.findUserById(userId);
        log.info("Look for items of user={}",user);
        return itemRepository.findItemsByOwner(user);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        log.info("Look for items by text={}",text);
        return itemRepository.searchItemsByText(text);
    }
}
