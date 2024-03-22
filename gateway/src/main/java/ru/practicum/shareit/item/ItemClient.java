package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdatingRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getAllByUserId(long userId) {
        return get("",userId);
    }

    public  ResponseEntity<Object> updateItem(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest){
        String path = String.format("/%d", itemId);
        return patch(path,userId,itemUpdatingRequest);
    }

    public ResponseEntity<Object> removeItem(long userId, long itemId){
        String path = String.format("/%d", itemId);
        return delete(path,userId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemByIdWithDate(long userId, long itemId){
        String path = String.format("/%d", itemId);
        return get(path,userId);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto text) {
        String path = String.format("%d/comment", itemId);
        return post(path, itemId, text);
    }
}
