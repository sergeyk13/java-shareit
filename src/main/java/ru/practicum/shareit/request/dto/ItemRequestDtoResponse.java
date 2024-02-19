package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseToRequestDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDtoResponse {
    @NotNull
    @Min(1)
    private long id;
    @NotBlank
    private String description;
    @NotNull
    private LocalDateTime created;
    @NotNull
    private List<ItemResponseToRequestDto> items;
}
