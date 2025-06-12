package jungwoonson.board.view.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {
    private final StringRedisTemplate redisTemplate;;

    // view::article::{article_id}::user::{user_id}::lock
    private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";

    public boolean tryLock(Long articleId, Long userId, Duration ttl) {
        String key = generateKey(articleId, userId);
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl);
    }

    private String generateKey(Long articleId, Long userId) {
        return KEY_FORMAT.formatted(articleId, userId);
    }
}
