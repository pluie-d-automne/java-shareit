package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private List<Item> items = new ArrayList<>();
    private Long idCounter = 0L;

    private final ItemMapper itemMapper;

    public ItemRepositoryImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(User user, ItemDto itemDto) {
        Item item = new Item(generateId(),
                user,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequest());
        items.add(item);
        log.info("Created item {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(User user, Long itemId, ItemDto itemDto) {
        Item itemFound = getItemById(itemId);

        if (! isOwner(user, itemFound)) {
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

    @Override
    public ItemDto findOne(Long itemId) {
        Item itemFound = getItemById(itemId);
        log.info("Found item {}", itemFound);
        return itemMapper.toItemDto(itemFound);
    }

    @Override
    public List<ItemDto> findItemsByOwner(User user) {
        log.info("Find items for user={}", user);
        return items.stream()
                .filter(item -> item.getOwner().equals(user))
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
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

    private Boolean isOwner(User user, Item item) {
        return item.getOwner().equals(user);
    }

    private Boolean isItemSearched(Item item, String text) {
        return item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase());
    }
}
