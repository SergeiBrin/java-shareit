package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.model.dto.ReqItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public ItemRequestClient(RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        String path = "";
        return get(path, userId);
    }

    public ResponseEntity<Object> getRequestByReqId(long userId, long requestId) {
        String path = "/" + requestId;
        return get(path, userId);
    }

    public ResponseEntity<Object> getRequestsFromOthers(long userId, int from, int size) {
        String path = "/all?from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> createRequest(long userId, ReqItemRequestDto itemRequestDto) {
        String path = "";
        return post(path, userId, itemRequestDto);
    }
}
