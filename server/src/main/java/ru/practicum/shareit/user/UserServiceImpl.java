package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoResponse;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional
    @Override
    public UserDtoResponse userCreate(UserDto userDto) {
        try {
            log.info("Save user: name:{}, email:{}", userDto.getName(), userDto.getEmail());
            return UserMapper.INSTANCE.modelToDto(repository.save(
                    UserMapper.INSTANCE.dtoToModel(userDto)
            ));
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateKeyViolation(e)) {
                log.error("Error creating user: {}", e.getMessage());
                throw new ConflictException("User with this email already exists");
            } else {
                throw e;
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDtoResponse getUser(long userId) {
        log.info("Return user: {}", userId);
        return UserMapper.INSTANCE.modelToDto(repository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User witch id:%d not found", userId))
        ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDtoResponse> getAll() {
        return UserMapper.INSTANCE.modelListToDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public UserDtoResponse updateUser(long userId, UserUpdateRequest userUpdateRequest) {
        Optional<User> user = repository.findById(userId);
        User updateUser;

        if (user.isPresent()) {
            updateUser = user.get();
        } else {
            throw new NotFoundException(String.format("User with ID: %d not found", userId));
        }

        Optional<String> name = Optional.ofNullable(userUpdateRequest.getName());
        Optional<String> email = Optional.ofNullable(userUpdateRequest.getEmail());

        if (name.isEmpty() && email.isEmpty()) {
            throw new ValidationException("At least one field for update should be provided.");
        }

        name.ifPresent(n -> {
            if (!n.isBlank()) {
                updateUser.setName(n);
            }
        });

        email.ifPresent(e -> {
            if (!e.isBlank()) {
                updateUser.setEmail(e);
            }
        });

        if (checkValidation(updateUser)) {
            log.info("Validation done");
        } else {
            throw new ValidationException("Validation error");
        }
        log.info("Update user: {}", userId);
        return UserMapper.INSTANCE.modelToDto(repository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User witch id:%d not found", userId))
        ));
    }

    @Transactional
    @Override
    public void removeUser(long userId) {
        log.info("Remove user: {}", userId);
        repository.deleteById(userId);
    }

    private boolean checkValidation(@Valid User user) {
        return true;
    }

    private boolean isDuplicateKeyViolation(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException
                && e.getCause().getMessage().contains("duplicate key value violates unique constraint");
    }
}
