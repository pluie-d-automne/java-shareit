package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userService.findUserById(userId);
        ItemRequest request = new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                LocalDateTime.now());
        itemRequestRepository.save(request);
        log.info("Saved new item request {}", request);
        return itemRequestMapper.itemRequestToItemRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> findRequestsByUser(Long userId) {
        log.info("Look for item requests by user_id={}",userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(userId);
        return requests.stream().map(itemRequestMapper::itemRequestToItemRequestDto).toList();
    }

    @Override
    public List<ItemRequestDto> findRequestsOtherUsers(Long userId) {
        log.info("Look for item requests by users other than user_id={}",userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNot(userId);
        return requests.stream().map(itemRequestMapper::itemRequestToItemRequestDto).toList();

    }

    @Override
    public ItemRequestWithItemsDto findOne(long itemRequestId) {
        log.info("Look for item request by id={}",itemRequestId);
        ItemRequest request = findItemRequestById(itemRequestId);
        ItemRequestWithItemsDto requestDto = itemRequestMapper.itemRequestToItemRequestWithItemsDto(request);
        List<Item> items = itemRepository.findByRequestId(itemRequestId);
        requestDto.setItems(items.stream().map(itemRequestMapper::itemToItemForRequestDto).toList());
        return requestDto;
    }

    private ItemRequest findItemRequestById(Long itemRequestId) {
        Optional<ItemRequest> request = itemRequestRepository.findById(itemRequestId);

        if (request.isEmpty()) {
            throw new NotFoundException("Item request with id=" + itemRequestId + " was not found.");
        } else {
            ItemRequest requestFound = request.get();
            log.info("Found item request {} by id {}", requestFound, itemRequestId);
            return requestFound;
        }
    }
}
