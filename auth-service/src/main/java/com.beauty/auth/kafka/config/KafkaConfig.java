package com.beauty.auth.kafka.config;

import com.beauty.auth.kafka.dto.LogMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.topic.log}")
    private String logTopic;

    @Bean
    public ProducerFactory<String, LogMessage> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Настройки надежности
        config.put(ProducerConfig.ACKS_CONFIG, "all");//требуется подтверждение от всех брокеров
        config.put(ProducerConfig.RETRIES_CONFIG, 3);//количество попыток отправить сообщение, перед тем как отметить его как неудачное
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);//время ожидания перед повторной попыткой отправить неудавшийся запрос
        // Настройки производительности
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);//максимальный размер пакета данных, отправляемого в одном запросе - 16кб
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5);//максимальное время ожидания дополнительных сообщений перед отправкой партии 5 мс
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);// общий объём памяти, доступный продюсеру для буферизации сообщений - 32 мб (по умолчанию)
        // Настройки идемпотентности
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);//Идемпотентность позволяет избежать отправки дублированных сообщений даже при сбоях в сети, повторных попытках или сбоях брокера
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, LogMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic logTopic() {
        return TopicBuilder.name(logTopic)
                .partitions(3)
                .replicas(1)//тема не реплицирована
                .configs(Map.of(
                        "retention.ms", "604800000", // время хранения сообщений в топике (теме) перед их удалением. 7 дней
                        "cleanup.policy", "delete"
                ))//удалять после истечения времени
                .build();
    }
}
