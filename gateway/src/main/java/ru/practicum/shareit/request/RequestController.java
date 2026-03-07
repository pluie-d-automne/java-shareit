package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestBody RequestDto requestDto) {
        return requestClient.addRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestsByUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return requestClient.findRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findRequestsOtherUsers(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return requestClient.findRequestsOtherUsers(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findRequestById(@PathVariable(name = "id") long itemRequestId) {
        return requestClient.findRequestById(itemRequestId);
    }
}
