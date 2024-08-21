package koh.service.auth.kafka;

import koh.service.auth.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class KafkaExecutor {
    Map<String, MessageHandler> topicHandlers;
    KafkaConsumer<String, String> consumer;
    KafkaProducer<String, String> producer;

    public KafkaExecutor(KafkaConsumer<String, String> consumer, KafkaProducer<String, String> producer) {
        this.topicHandlers = new HashMap<>();
        this.consumer = consumer;
        this.producer = producer;
    }

    public void addHandler(
            Topic topic, MessageHandler handler
    ) {
        topicHandlers.put(topic.name(), handler);
    }

    void poll() {
        try {
            Duration pollDuration = Duration.of(2500, ChronoUnit.MILLIS);
            while (true) {
                ConsumerRecords<String, String> messages = consumer.poll(pollDuration);
                for (ConsumerRecord<String, String> m : messages) {
                    log.info("Handling message topic: {}", m.topic());
                    log.info("Message key: {} \n value: {}", m.key(), m.value());

                    this.consume(m).ifPresent(producer::send);

                    log.info("Handled message topic: {}", m.topic());
                }

                if (Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().join();
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Kafka executor has been interrupted", e);
        }
    }

    Optional<ProducerRecord<String, String>> consume(ConsumerRecord<String, String> message) {
        try {
            return Optional.ofNullable(this.topicHandlers.getOrDefault(message.topic(), m -> null).handle(message));
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return Optional.empty();
        }
    }

    public void exec() {
        consumer.subscribe(this.topicHandlers.keySet());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::poll);
    }
}
