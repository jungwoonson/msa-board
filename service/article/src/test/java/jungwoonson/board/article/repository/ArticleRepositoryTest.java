package jungwoonson.board.article.repository;

import jungwoonson.board.article.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findAllTest() {
        List<Article> articles = articleRepository.findAll(1L, 1499970L, 30L);
        log.info("articles.size = {}", articles.size());
        for (Article article : articles) {
            log.info("article = {}", article);
        }
    }

    @Test
    void countTest() {
        log.info("articleRepository.count = {}", articleRepository.count(1L, 100000L));
    }

    @Test
    void findInfiniteScrollTest() {
        List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
        for (Article article : articles) {
            log.info("article = {}", article);
        }

        Long lastArticleId = articles.getLast().getArticleId();
        List<Article> nextArticles = articleRepository.findAllInfiniteScroll(1L, 30L, lastArticleId);
        for (Article article : nextArticles) {
            log.info("next article = {}", article);
        }
    }
}