package koh.service.auth.handler;

import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.kafka.message.AuthenticationMessage;
import koh.service.auth.kafka.message.AuthorizationMessage;
import koh.service.auth.kafka.message.StatusMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.secure.Password;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.Optional;

import static koh.service.auth.kafka.KafkaRespTopic.TOPIC_AUTH_LOGIN_RESPONSE;

@Slf4j
public class AuthenticateHandler extends AbstractAuthHandler implements MessageHandler {

    public AuthenticateHandler(Jwt jwt, KafkaProducerWorker response) {
        super(jwt, response);
    }

    @Override
    public void handle(ConsumerRecord<String, String> message)
            throws IOException {
        String requestId = message.key();

        AuthenticationMessage m = JsonTools.fromJson(message.value(), AuthenticationMessage.class);
        String email = m.getEmail();
        String password = m.getPassword();

        String encryptedPassword = new Password(password).getSecuredDigest();
        Optional<UserRecord> user = new UserRepository().getUserByEmailAndPassword(email, encryptedPassword);

        if (user.isPresent()) {
            AuthorizationMessage authorizationMessage = new AuthorizationMessage();
            authorizationMessage.setAccessToken(jwt.generateAccess(email));
            authorizationMessage.setRefreshToken(jwt.generateRefresh(email));

            bus.respond(TOPIC_AUTH_LOGIN_RESPONSE, requestId, authorizationMessage);
        } else {
            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus("Email or password is incorrect!");
            bus.respond(TOPIC_AUTH_LOGIN_RESPONSE, requestId, statusMessage);
        }
    }
}
