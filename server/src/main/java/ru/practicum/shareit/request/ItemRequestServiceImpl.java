package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public ResponseEntity<ItemRequestDto> requestCreate(long userId, ItemRequestDto requestDto) {
        User user = checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.dtoToModel(requestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setCreator(user);

        requestRepository.save(itemRequest);
        log.info("create request: {}", itemRequest.getId());
        return new ResponseEntity<>(ItemRequestMapper.INSTANCE.modelToDto(itemRequest),
                HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<ItemRequestDtoResponse>> getRequestByUser(long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequestList = requestRepository.getItemRequestByCreatorIdOrderByCreated(userId);
        return new ResponseEntity<>(ItemRequestMapper.INSTANCE.modelListToResponseDtoList(itemRequestList),
                HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<ItemRequestDtoResponse>> getAllRequests(long userId, int from, int size) {
        checkUser(userId);
        Sort sortByStart = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sortByStart);
        Page<ItemRequest> requestPage = requestRepository.findItemRequestsNotCreatedByUserId(userId,page);
        return new ResponseEntity<>(
                requestPage.stream()
                        .map(ItemRequestMapper.INSTANCE::modelToResponseDto)
                        .collect(Collectors.toList()), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<ItemRequestDtoResponse> getRequestById(long userId, long requestId) {
        checkUser(userId);
        return new ResponseEntity<>(
                ItemRequestMapper.INSTANCE.modelToResponseDto(requestRepository.findById(requestId).orElseThrow(() ->
                        new NotFoundException(String.format("Request ID:%d not found", requestId)))
                ),
                HttpStatus.OK);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User ID:%d not found", userId)));
    }
}
