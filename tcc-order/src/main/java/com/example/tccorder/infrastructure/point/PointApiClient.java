package com.example.tccorder.infrastructure.point;

import com.example.tccorder.infrastructure.point.dto.PointReserveApiRequest;
import com.example.tccorder.infrastructure.point.dto.PointReserveCancelApiRequest;
import com.example.tccorder.infrastructure.point.dto.PointReserveConfirmApiRequest;
import org.springframework.web.client.RestClient;

public class PointApiClient {

    private final RestClient restClient;

    public PointApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void reservePoint(PointReserveApiRequest request) {
        restClient.post()
                .uri("/point/reserve")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void confirmPoint(PointReserveConfirmApiRequest request) {
        restClient.post()
                .uri("/point/confirm")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void cancelPoint(PointReserveCancelApiRequest request) {
        restClient.post()
                .uri("/point/cancel")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
