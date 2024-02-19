package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ResponseEntity<ItemRequestDto> requestCreate(long userId, ItemRequestDto requestDto);

    ResponseEntity<List<ItemRequestDtoResponse>> getRequestByUser(long userId);

    ResponseEntity<List<ItemRequestDtoResponse>> getAllRequests(long userId, int from, int size);

    ResponseEntity<ItemRequestDtoResponse> getRequestById(long userId, long requestId);
}
