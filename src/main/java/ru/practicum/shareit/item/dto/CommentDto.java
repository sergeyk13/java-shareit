package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDto {
    @NotBlank
    @Size(max = 200)
    private String text;
}
