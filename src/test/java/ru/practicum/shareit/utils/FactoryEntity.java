package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FactoryEntity {

    public static UserDto creatRandomUserDto() {
        Random random = new Random();
        int i = random.nextInt(10) + 1;
        String nameAndId = "Name" + i;
        String email = "email" + i + "@mail.com";

        return new UserDto(nameAndId, email);
    }

    public static UserDto creatRandomUserDto(long id) {
        Random random = new Random();
        String nameAndId = "Name" + id;
        String email = "email" + id + "@mail.com";

        return new UserDto(nameAndId, email);
    }

    public static User creatRandomUser() {
        Random random = new Random();
        long userId = random.nextInt(10) + 1;
        String nameAndId = "Name" + userId;
        String email = "email" + userId + "@mail.com";

        return new User(userId, nameAndId, email);
    }

    public static User creatRandomUserWithoutId() {
        User user = new User();
        user.setName("Name");
        user.setEmail("email@mail.com");
        return user;
    }

    public static User creatRandomUser(long id) {
        Random random = new Random();
        String nameAndId = "Name" + id;
        String email = "email" + id + "@mail.com";

        return new User(id, nameAndId, email);
    }

    public static Item createRandomItem(long userId) {
        Random random = new Random();
        long itemId = random.nextInt(10) + 1;
        String name = "Name" + itemId;
        String description = "desc" + itemId;
        return new Item(itemId, userId, name, description, true, null);
    }

    public static Item createRandomItemWithOutId(long userId) {
        Random random = new Random();
        long itemId = random.nextInt(10) + 1;
        String name = "Name" + itemId;
        String description = "desc" + itemId;
        return new Item(null, userId, name, description, true, null);
    }

    public static ItemDto createRandomItemDto(Long itemId) {

        String name = "Name" + itemId;
        String description = "desc" + itemId;
        return new ItemDto(itemId, name, description, true, null);
    }

    public static Item createRandomItem(long userId, Long requestId) {
        Random random = new Random();
        long itemId = random.nextInt(10) + 1;
        String name = "Name" + itemId;
        String description = "desc" + itemId;
        return new Item(itemId, userId, name, description, true, requestId);
    }

    public static BookingDto createRandomBookingDto(Item item) {
        Random random = new Random();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(random.nextInt(10) + 1);
        return new BookingDto(item.getId(), start, end);
    }

    public static Booking createRandomBooking(Item item, User booker) {
        Random random = new Random();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(random.nextInt(10) + 1);
        BookingState status = BookingState.values()[random.nextInt(BookingState.values().length)];
        BookingState state = BookingState.values()[random.nextInt(BookingState.values().length)];

        return new Booking(random.nextInt(10) + 1, item, booker, start, end, status, state);
    }

    public static List<Booking> generateRandomBookingList(int count, Item item, User booker) {
        List<Booking> bookingList = new ArrayList<>();
        Random random = new Random();
        long lowerBound = 1L;
        long upperBound = 11L;
        for (int i = 0; i < count; i++) {
            if (i > 1) {
                Item newItem = new Item((long) (random.nextInt(10) + 1),
                        item.getOwnerId(),
                        item.getName(),
                        item.getDescription(),
                        random.nextBoolean(),
                        item.getRequestId());
                User newBooker = new User(random.longs(lowerBound, upperBound).findAny().getAsLong(), booker.getName(), booker.getEmail());
                bookingList.add(createRandomBooking(newItem, newBooker));
            } else {
                bookingList.add(createRandomBooking(item, booker));
            }
        }
        return bookingList;
    }
}
