package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public BookingClient(RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getBookingById(long bookingId, long userId) {
        String path = "/" + bookingId;
        return get(path, userId);
    }

    public ResponseEntity<Object> getUserBookingsByState(long userId, String state, int from, int size) {
        String path = "?state={state}&from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookingsByState(long userId, String state, int from, int size) {
        String path = "/owner?state={state}&from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> createBooking(long userId, ReqBookingDto reqBookingDto) {
        String path = "";
        return post(path, userId, reqBookingDto);
    }

    public ResponseEntity<Object> updateBooking(long userId, long bookingId, Boolean approved) {
        String path = "/{bookingId}?approved={approved}";

        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved);

        return patch(path, userId, parameters, null);
    }
}
