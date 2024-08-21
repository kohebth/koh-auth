package koh.service.auth.handler;

import io.jsonwebtoken.Claims;
import koh.service.auth.kafka.Topic;
import koh.service.auth.message.AuthorizationMessage;
import koh.service.auth.message.RefreshMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;

@Slf4j
public class RefreshHandler extends AbstractAuthHandler implements MessageHandler {
    public RefreshHandler(Jwt jwt) {
        super(jwt);
    }

    @Override
    public ProducerRecord<String, String> handle(ConsumerRecord<String, String> message)
            throws IOException {
        RefreshMessage m = JsonTools.fromJson(message.value(), RefreshMessage.class);

        String email = m.getEmail();
        String refreshtToken = m.getRefreshToken();

        Claims c = jwt.verify(refreshtToken);


        if (c.getSubject().equals(email)) {
            AuthorizationMessage authorizationMessage = new AuthorizationMessage();
            authorizationMessage.setAccessToken(jwt.generateAccess(email));
            authorizationMessage.setAccessToken(jwt.generateRefresh(email));

            return new ProducerRecord<>(Topic.AUTHORIZATION.name(), email, JsonTools.toJson(authorizationMessage));
        }

        return null;
    }
}
