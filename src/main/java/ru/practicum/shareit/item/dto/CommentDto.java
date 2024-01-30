package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CommentDto {
    @NotNull
    @Size(max = 200)
    private String text;
}
