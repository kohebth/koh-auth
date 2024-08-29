package koh.service.auth.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;

public interface MessageHandler {
    void handle(ConsumerRecord<String, String> message)
            throws IOException;
}
