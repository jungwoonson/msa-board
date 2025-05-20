package jungwoonson.board.comment.api;

import jungwoonson.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.printf("commentId=%s%n", response1.getCommentId());
        System.out.printf("\tcommentId=%s%n", response2.getCommentId());
        System.out.printf("\tcommentId=%s%n", response3.getCommentId());
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        restClient.get()
                .uri("/v1/comments/{commentId}", 183142762104737792L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + restClient.get());
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 183142762649997312L)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }
}
