package jungwoonson.board.hotarticle.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HotArticleListRepository {

    private final StringRedisTemplate redisTemplate;

    // hot-article::list::{yyyyMMdd}
    private static final String KEY_FORMAT = "hot-article::list::%s";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void add(Long articleId, LocalDateTime time, Long score, Long limit, Duration ttl) {
        // executePipelined():
        // 인자로 받은 RedisCallback 안에서 여러 명령을 실행하면,
        // 그 명령들이 파이프라인에 쌓였다가 한 번에 Redis 서버로 전송됨
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(time);
            // zAdd(), zRemRange()를 사용해서 Sorted-Set 자료구조 사용
            conn.zAdd(key, score, String.valueOf(articleId));
            conn.zRemRange(key, 0, - limit - 1); // 최대 limit 개만 남기고 나머지 제거
            conn.expire(key, ttl.toSeconds());
            return null;
        });
    }

    private String generateKey(LocalDateTime time) {
        return generateKey(TIME_FORMATTER.format(time));
    }

    private String generateKey(String dateStr) {
        return KEY_FORMAT.formatted(dateStr);
    }

    public List<Long> readAll(String dateStr) {
        return redisTemplate.opsForZSet() // ZSET(정렬된 집합) 관련 명령어를 사용할 수 있는 API를 가져옴
                .reverseRangeWithScores(generateKey(dateStr), 0,
                        -1) // 높은 score → 낮은 score 순으로 전체 조회
                .stream()
                .peek(tuple -> log.info("[HotArticleListRepository.readAll] articleId={}, score={}",
                        tuple.getValue(), tuple.getScore())) // 각 요소를 처리하면서 로깅
                .map(ZSetOperations.TypedTuple::getValue) // value(게시글 ID 문자열)만 꺼냄
                .map(Long::valueOf)
                .toList();
    }
}
