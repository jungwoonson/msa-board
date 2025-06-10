package jungwoonson.board.article.api;

import jungwoonson.board.article.entity.Article;
import jungwoonson.board.article.service.response.ArticlePageResponse;
import jungwoonson.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(178042441126211584L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        update(178042441126211584L);
        ArticleResponse response = read(178042441126211584L);
        System.out.println(response);
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2", "your content"))
                .retrieve();
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/articles/{articleId}", 178042441126211584L)
                .retrieve();
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<ArticleResponse> response = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        System.out.println("firstPage");
        for (ArticleResponse article : response) {
            System.out.println("articleId = " + article.getArticleId());
        }

        List<ArticleResponse> response2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=" + response.getLast().getArticleId())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        System.out.println("secondPage");
        for (ArticleResponse article : response2) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void countTest() {
        ArticleResponse articleResponse = create(
                new ArticleCreateRequest("hi", "my content", 1L, 2L));

        Long count = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", articleResponse.getBoardId())
                .retrieve()
                .body(Long.class);

        System.out.println("boardId = " + articleResponse.getBoardId() + " count = " + count);

        restClient.delete()
                .uri("/v1/articles/{articleId}", articleResponse.getArticleId())
                .retrieve();

        count = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", articleResponse.getBoardId())
                .retrieve()
                .body(Long.class);

        System.out.println("boardId = " + articleResponse.getBoardId() + " count = " + count);
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
