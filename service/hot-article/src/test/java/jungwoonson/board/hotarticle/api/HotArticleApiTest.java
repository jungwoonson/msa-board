package jungwoonson.board.hotarticle.api;

import jungwoonson.board.hotarticle.service.response.HotArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class HotArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9004");

    @Test
    void readAllTest() {
        String today = "20250826";

        List<HotArticleResponse> responses = restClient.get()
                .uri("/v1/hot-articles/articles/date/{dateStr}", today)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        for (HotArticleResponse response : responses) {
            System.out.println("response = " + response);
        }
    }
}
