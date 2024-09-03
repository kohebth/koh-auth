package koh.service.auth.handler;

import io.jsonwebtoken.Claims;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.kafka.message.AuthorizationMessage;
import koh.service.auth.kafka.message.RefreshMessage;
import koh.service.auth.kafka.message.StatusMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

import static koh.service.auth.kafka.KafkaRespTopic.TOPIC_AUTH_REFRESH_RESPONSE;

@Slf4j
public class RefreshHandler extends AbstractAuthHandler implements MessageHandler {

    public RefreshHandler(Jwt jwt, KafkaProducerWorker response) {
        super(jwt, response);
    }

    @Override
    public void handle(ConsumerRecord<String, String> message)
            throws IOException {

        String requestId = message.key();

        RefreshMessage m = JsonTools.fromJson(message.value(), RefreshMessage.class);

        Long userId = m.getUserId();
        String email = m.getEmail();
        String refreshToken = m.getRefreshToken();

        Claims c = jwt.verify(refreshToken);


        if (c.getSubject().equals(email)) {
            AuthorizationMessage authorizationMessage = new AuthorizationMessage();
            authorizationMessage.setAccessToken(jwt.generateAccess(email, userId));

            bus.respond(TOPIC_AUTH_REFRESH_RESPONSE, requestId, authorizationMessage);
        } else {
            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus("Invalid refresh token!");

            bus.respond(TOPIC_AUTH_REFRESH_RESPONSE, requestId, statusMessage);
        }
    }
}
