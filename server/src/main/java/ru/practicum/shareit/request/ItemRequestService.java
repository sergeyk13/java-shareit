package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto requestCreate(long userId, ItemRequestDto requestDto);

    List<ItemRequestDtoResponse> getRequestByUser(long userId);

    List<ItemRequestDtoResponse> getAllRequests(long userId, int from, int size);

    ItemRequestDtoResponse getRequestById(long userId, long requestId);
}
