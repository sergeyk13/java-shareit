package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.utils.FactoryEntity;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private UserDto ownerDto;
    private UserDto userDto2;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        ownerDto = FactoryEntity.creatRandomUserDto();
        userDto2 = FactoryEntity.creatRandomUserDto();
        itemDto = FactoryEntity.createRandomItemDto(1L);
    }

    @Test
    void getBookingById() {

        User owner = UserMapper.INSTANCE.dtoToModel(ownerDto);
        em.persist(owner);

        User booker = UserMapper.INSTANCE.dtoToModel(userDto2);
        em.persist(booker);

        Item item = ItemMapper.INSTANCE.dtoToModel(itemDto, owner.getId());
        em.persist(item);

        BookingDto bookingDto = FactoryEntity.createRandomBookingDto(item);
        long bookingId = bookingService.bookingCreate(booker.getId(), bookingDto).getBody().getId();

        ResponseEntity<BookingDtoResponse> response = bookingService.getBookingById(booker.getId(),bookingId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().getBooker().getName(), equalTo(booker.getName()));
        assertThat(response.getBody().getItem().getName(), equalTo(item.getName()));
    }
}