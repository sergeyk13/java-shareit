package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoResponse {
    @NotNull
    @Min(1)
    private long id;
    @NotBlank
    @Size(max = 200)
    private String text;
    @NotBlank
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;
}
