package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class ItemDaoImpl implements ItemDao {
    Collection<Item> items = new ArrayList<>();
    Long idCounter = 0L;

    private final ItemMapper itemMapper;

    public ItemDaoImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = new Item(generateId(), userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        items.add(item);
        log.info("Created item {}", item);
        return itemMapper.toItemDto(item);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item itemFound = getItemById(itemId);

        if (! isOwner(userId, itemFound)) {
            throw new UnauthorizedException("Пользователь не является владельцем вещи");
        }

        String newName = itemDto.getName();
        String newDescription = itemDto.getDescription();
        Boolean newAvailable = itemDto.getAvailable();

        if (newName != null) {
            itemFound.setName(newName);
        }

        if (newDescription != null) {
            itemFound.setDescription(newDescription);
        }

        if (newAvailable != null) {
            itemFound.setAvailable(newAvailable);
        }

        log.info("Updated item {}", itemFound);
        return itemMapper.toItemDto(itemFound);
    }

    public ItemDto findOne(Long itemId) {
        Item itemFound = getItemById(itemId);
        log.info("Found item {}", itemFound);
        return itemMapper.toItemDto(itemFound);
    }

    public Collection<ItemDto> findItemsByOwner(Long userId) {
        log.info("Find items for userId={}", userId);
        return items.stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(itemMapper::toItemDto)
                .toList();
    }

    public Collection<ItemDto> itemTextSearch(String text) {
        log.info("Find items by text={}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.stream()
                .filter(item -> isItemSearched(item, text))
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .toList();
    }

    private Long generateId() {
        idCounter += 1L;
        log.info("Generated new itemId={}", idCounter);
        return idCounter;
    }

    private Item getItemById(Long itemId) {
        Optional<Item> itemFound = items.stream().filter(item -> item.getId().equals(itemId)).findFirst();

        if (itemFound.isEmpty()) {
            throw new NotFoundException("Вещь с id=" + itemId + "не найдена.");
        } else {
            return itemFound.get();
        }
    }

    private Boolean isOwner(Long userId, Item item) {
        return item.getOwnerId().equals(userId);
    }

    private Boolean isItemSearched(Item item, String text) {
        return item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase());
    }
}
