package jungwoonson.board.hotarticle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

/**
 * Kafka 관련 설정 클래스
 * - Kafka Listener 컨테이너의 동작 방식을 설정
 * - @KafkaListener 어노테이션으로 메시지를 소비할 때 이 Factory가 사용됨
 */
@Configuration // Spring 설정 클래스임을 명시 (스프링이 Bean 등록 시 인식)
public class KafkaConfig {

    /**
     * Kafka Listener Container Factory 빈 정의
     *
     * @param consumerFactory ConsumerFactory<String, String>
     *                        - KafkaConsumer를 생성하는 팩토리
     *                        - Spring Boot 자동설정이나 직접 정의한 ConsumerFactory가 주입됨
     * @return ConcurrentKafkaListenerContainerFactory<String, String>
     * - 실제로 @KafkaListener가 사용할 Listener Container Factory
     * <p>
     * 주요 설정:
     * - setConsumerFactory(consumerFactory): KafkaConsumer 생성 방식을 지정
     * - setAckMode(MANUAL): 메시지 수동 커밋 모드 설정
     * (메시지를 받은 후 직접 ack.acknowledge() 호출해야 offset이 커밋됨)
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory
    ) {
        // Kafka Listener Container Factory 생성
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        // ConsumerFactory를 지정 (KafkaConsumer 생성 로직을 위임)
        factory.setConsumerFactory(consumerFactory);
        // 수동 커밋 모드로 지정
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);

        // 완성된 Factory를 Bean으로 등록
        return factory;
    }
}