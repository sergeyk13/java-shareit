package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private final ItemDto itemDto = new ItemDto(1L, "TestItem", "Description", true, null);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @Test
    void testItemCreate() throws Exception {
        long userId = 1L;
        when(itemService.saveItem(eq(userId), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllByUserId() throws Exception {
        long userId = 1L;
        List<ItemResponseDto> items = Arrays.asList(
                new ItemResponseDto(1L, "Item1", "Description1", true, null, null, null),
                new ItemResponseDto(2L, "Item2", "Description2", true, null, null, null)
        );

        when(itemService.getAllByUserId(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(items.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(items.get(1).getDescription()));
    }

    @Test
    void testUpdateItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemUpdatingRequest updatingRequest = new ItemUpdatingRequest("UpdatedItem", "UpdatedDescription", true);

        when(itemService.prepareUpdating(eq(userId), eq(itemId), any(ItemUpdatingRequest.class))).thenReturn(new Item());
        when(itemService.updateItem(eq(userId), eq(itemId), any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(updatingRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1)).removeItem(userId, itemId);
    }

    @Test
    void testSearchItems() throws Exception {
        String searchText = "test";
        List<ItemDto> searchResults = Arrays.asList(
                new ItemDto(1L, "Item1", "Description1", true, null),
                new ItemDto(2L, "Item2", "Description2", true, null)
        );

        when(itemService.searchItems(searchText, 0, 10)).thenReturn(searchResults);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(searchResults.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(searchResults.get(0).getDescription()))
                .andExpect(jsonPath("$[1].name").value(searchResults.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(searchResults.get(1).getDescription()));
    }

    @Test
    void testGetItemByIdWithDate() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemResponseDto itemResponseDto = new ItemResponseDto(
                itemId, "TestItem", "TestDescription", null, null, null, null
        );

        when(itemService.getItemByIdWithDate(itemId, userId)).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()));
    }

    @Test
    void testCreateComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        CommentDto commentDto = new CommentDto("TestComment");
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse(1L, commentDto.getText(), "Name", LocalDateTime.now());

        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDtoResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
