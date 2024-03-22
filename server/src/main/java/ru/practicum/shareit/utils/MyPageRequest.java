package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class MyPageRequest extends PageRequest {
    protected MyPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static PageRequest of(int from, int size, Sort sort) {
        int page = from / size;
        return new MyPageRequest(page, size, sort);
    }
}
