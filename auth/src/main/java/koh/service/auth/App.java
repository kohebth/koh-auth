package koh.service.auth;

import koh.service.auth.handler.AuthenticateHandler;
import koh.service.auth.handler.RefreshHandler;
import koh.service.auth.handler.RegisterHandler;
import koh.service.auth.kafka.KafkaConfig;
import koh.service.auth.kafka.KafkaExecutor;
import koh.service.auth.kafka.Topic;
import koh.service.auth.secure.Jwt;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class App {
    final Jwt jwt;
    final KafkaConfig kafkaConfig;
    final KafkaConsumer<String, String> consumer;
    final KafkaProducer<String, String> producer;
    final KafkaExecutor kafkaExecutor;

    App()
            throws Exception {
        this.jwt = new Jwt(AppConfig.APP_PRIVATE_KEY_PATH, AppConfig.APP_PUBLIC_KEY_PATH);
        this.kafkaConfig = new KafkaConfig(AppConfig.KAFKA_HOST, AppConfig.KAFKA_PORT, AppConfig.KAFKA_GROUP);
        this.consumer = new KafkaConsumer<>(this.kafkaConfig.getConsumerProperties());
        this.producer = new KafkaProducer<>(this.kafkaConfig.getProducerProperties());
        this.kafkaExecutor = new KafkaExecutor(this.consumer, this.producer);
    }

    void start() {
        this.kafkaExecutor.addHandler(Topic.REGISTER, new RegisterHandler(jwt));
        this.kafkaExecutor.addHandler(Topic.AUTHENTICATION, new AuthenticateHandler(jwt));
        this.kafkaExecutor.addHandler(Topic.REFRESH_TOKEN, new RefreshHandler(jwt));

        this.kafkaExecutor.exec();
    }

    public static void main(String[] args)
            throws Exception {
        new App().start();
    }
}
