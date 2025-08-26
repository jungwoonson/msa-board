package jungwoonson.board.articleread.service.event.handler;

import jungwoonson.board.articleread.repository.ArticleIdListRepository;
import jungwoonson.board.articleread.repository.ArticleQueryModel;
import jungwoonson.board.articleread.repository.ArticleQueryModelRepository;
import jungwoonson.board.articleread.repository.BoardArticleCountRepository;
import jungwoonson.board.common.event.Event;
import jungwoonson.board.common.event.EventType;
import jungwoonson.board.common.event.payload.ArticleCreatedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {

    private final ArticleIdListRepository articleIdListRepository;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload = event.getPayload();
        // TODO: create()가 add() 보다 우선되는 이유가 있음
        articleQueryModelRepository.create(
                ArticleQueryModel.create(payload),
                Duration.ofDays(1)
        );
        articleIdListRepository.add(payload.getBoardId(), payload.getArticleId(), 1000L);
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }

    @Override
    public boolean supports(Event<ArticleCreatedEventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }
}
