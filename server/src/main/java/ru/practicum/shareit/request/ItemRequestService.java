package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findRequestsByUser(Long userId);

    List<ItemRequestDto> findRequestsOtherUsers(Long userId);

    ItemRequestWithItemsDto findOne(long itemRequestId);
}
