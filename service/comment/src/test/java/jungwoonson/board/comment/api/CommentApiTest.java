package jungwoonson.board.comment.api;

import jungwoonson.board.comment.service.response.CommentPageResponse;
import jungwoonson.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

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

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.printf("commentId=%s%n", comment.getCommentId());
        }

        // 1번 페이지 수행 결과
        //commentId=183147081874415616
        //	commentId=183147081907970049
        //commentId=183147081874415617
        //	commentId=183147081903775745
        //commentId=183147081874415618
        //	commentId=183147081903775747
        //commentId=183147081874415619
        //	commentId=183147081903775746
        //commentId=183147081874415620
        //	commentId=183147081903775749
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> response1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        System.out.println("firstPage");
        for (CommentResponse comment : response1) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.printf("commentId=%s%n", comment.getCommentId());
        }

        Long lastParentCommentId = response1.getLast().getParentCommentId();
        Long lastCommentId = response1.getLast().getCommentId();

        List<CommentResponse> response2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s".formatted(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        System.out.println("firstPage");
        for (CommentResponse comment : response2) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.printf("commentId=%s%n", comment.getCommentId());
        }
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
