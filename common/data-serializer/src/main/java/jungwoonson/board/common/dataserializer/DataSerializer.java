package jungwoonson.board.common.dataserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class DataSerializer {

    private static final ObjectMapper objectMapper = initialize();

    private static ObjectMapper initialize() {
        return new ObjectMapper()
                // 시간 관련 직력화 처리를 위한 모듈 등록
                // 기본적으로 지원하지 않는 이유? Jackson은 2009년 부터 사용, java.time 패키지는 2014년에 추가됨
                .registerModule(new JavaTimeModule())
                // 역직렬화(Deserialization) 과정에서 무시(ignore) 처리된 프로퍼티를 만나면 어떻게 할지를 결정하는 옵션
                // 무시된 프로퍼티가 JSON 에 있어도 예외를 던지지 않고 그냥 무시
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    public static <T> T deserialize(String data, Class<T> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T deserialize(Object data, Class<T> clazz) {
        return objectMapper.convertValue(data, clazz);
    }

    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("[DataSerializer.serialize] object={}", object, e);
            return null;
        }
    }
}
