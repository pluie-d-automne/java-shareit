package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository  extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long userId);

    List<Item> findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(String name, String desc);
}
