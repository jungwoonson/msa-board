package jungwoonson.board.article.service;

import jakarta.transaction.Transactional;
import jungwoonson.board.article.entity.Article;
import jungwoonson.board.article.entity.BoardArticleCount;
import jungwoonson.board.article.repository.ArticleRepository;
import jungwoonson.board.article.repository.BoardArticleCountRepository;
import jungwoonson.board.article.service.request.ArticleCreateRequest;
import jungwoonson.board.article.service.request.ArticleUpdateRequest;
import jungwoonson.board.article.service.response.ArticlePageResponse;
import jungwoonson.board.article.service.response.ArticleResponse;
import jungwoonson.board.common.event.EventType;
import jungwoonson.board.common.event.payload.ArticleCreatedEventPayload;
import jungwoonson.board.common.event.payload.ArticleDeletedEventPayload;
import jungwoonson.board.common.event.payload.ArticleUpdatedEventPayload;
import jungwoonson.board.common.outboxmessagerelay.OutboxEventPublisher;
import jungwoonson.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId())
        );
        int result = boardArticleCountRepository.increase(article.getBoardId());
        if (result == 0) {
            boardArticleCountRepository.save(BoardArticleCount.init(article.getBoardId(), 1L));
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.getTitle(), request.getContent());

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);
        boardArticleCountRepository.decrease(article.getBoardId());

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );
    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticlePageResponse.of(
                articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
    }

    public List<ArticleResponse> readInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        return findAllInfiniteScroll(boardId, pageSize, lastArticleId).stream()
                .map(ArticleResponse::from)
                .collect(Collectors.toList());
    }

    private List<Article> findAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        if (lastArticleId == null) {
            return articleRepository.findAllInfiniteScroll(boardId, pageSize);
        }
        return articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);
    }

    public Long count(Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
