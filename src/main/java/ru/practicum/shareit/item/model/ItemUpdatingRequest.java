package ru.practicum.shareit.item.model;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class ItemUpdatingRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @BooleanFlag
    private Boolean available;
}
