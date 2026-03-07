package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest);

    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestWithItemsDto  itemRequestToItemRequestWithItemsDto(ItemRequest itemRequest);

    @Mapping(target = "ownerId", source = "item.owner.id")
    ItemForRequestDto itemToItemForRequestDto(Item item);
}
