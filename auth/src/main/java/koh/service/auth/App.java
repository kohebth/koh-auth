package koh.service.auth;

import koh.db.hub.DataHub;
import koh.service.auth.handler.AuthenticateHandler;
import koh.service.auth.handler.RefreshHandler;
import koh.service.auth.handler.RegisterHandler;
import koh.service.auth.kafka.KafkaConfig;
import koh.service.auth.kafka.KafkaConsumerWorker;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.secure.Jwt;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import static koh.service.auth.kafka.KafkaReqTopic.*;

public class App {
    final Jwt jwt;
    final KafkaConfig kafkaConfig;
    final KafkaConsumerWorker kafkaConsumerWorker;
    final KafkaProducerWorker kafkaProducerWorker;

    App()
            throws Exception {
        this.jwt = new Jwt(AppConfig.APP_PRIVATE_KEY_PATH, AppConfig.APP_PUBLIC_KEY_PATH);
        this.kafkaConfig = new KafkaConfig(AppConfig.KAFKA_HOST, AppConfig.KAFKA_PORT, AppConfig.KAFKA_GROUP);

        this.kafkaConsumerWorker = new KafkaConsumerWorker(new KafkaConsumer<>(kafkaConfig.getConsumerProperties()));
        this.kafkaProducerWorker = new KafkaProducerWorker(new KafkaProducer<>(kafkaConfig.getProducerProperties()));
    }

    public static void main(String[] args)
            throws Exception {
        DataHub.connect(
                AppConfig.MARIADB_HOST,
                AppConfig.MARIADB_PORT,
                AppConfig.MARIADB_USER,
                AppConfig.MARIADB_PASSWORD,
                AppConfig.MARIADB_DATABASE
        );
        new App().start();
    }

    void start() {
        kafkaConsumerWorker.addHandler(TOPIC_AUTH_REGISTER_REQUEST, new RegisterHandler(jwt, kafkaProducerWorker));
        kafkaConsumerWorker.addHandler(TOPIC_AUTH_LOGIN_REQUEST, new AuthenticateHandler(jwt, kafkaProducerWorker));
        kafkaConsumerWorker.addHandler(TOPIC_AUTH_REFRESH_REQUEST, new RefreshHandler(jwt, kafkaProducerWorker));

        kafkaConsumerWorker.exec();
    }
}
