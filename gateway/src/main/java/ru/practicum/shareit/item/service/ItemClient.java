package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ReqCommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public ItemClient(RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        String path = "/" + itemId;

        return get(path, userId);
    }

    public ResponseEntity<Object> getAllItemsByUser(long userId, int from, int size) {
        String path = "?from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, ReqCommentDto text) {
        String path = "/" + itemId + "/comment";
        return post(path, userId, text);
    }

    public ResponseEntity<Object> searchAvailableItems(String text, int from, int size) {
        String path = "/search?text={text}&from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);

        return get(path, null, parameters);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        String path = "";
        return post(path, userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        String path = "/" + itemId;
        return patch(path, userId, itemDto);
    }
}


