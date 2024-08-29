package koh.service.auth.handler;

import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.secure.Jwt;

public abstract class AbstractAuthHandler {
    final Jwt jwt;
    final KafkaProducerWorker bus;

    protected AbstractAuthHandler(Jwt jwt, KafkaProducerWorker bus) {
        this.jwt = jwt;
        this.bus = bus;
    }
}
