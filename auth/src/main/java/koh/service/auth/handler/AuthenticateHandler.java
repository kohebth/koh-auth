package koh.service.auth.handler;

import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.Topic;
import koh.service.auth.message.AuthenticationMessage;
import koh.service.auth.message.AuthorizationMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.secure.Password;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class AuthenticateHandler extends AbstractAuthHandler implements MessageHandler {
    public AuthenticateHandler(Jwt jwt) {
        super(jwt);
    }

    @Override
    public ProducerRecord<String, String> handle(ConsumerRecord<String, String> message)
            throws IOException {
        AuthenticationMessage m = JsonTools.fromJson(message.value(), AuthenticationMessage.class);

        String email = m.getEmail();
        String password = m.getPassword();

        String encryptedPassword = new Password(password).getSecuredDigest();
        Optional<UserRecord> user = new UserRepository().getUserByEmailAndPassword(email, encryptedPassword);

        if (user.isPresent()) {
            AuthorizationMessage authorizationMessage = new AuthorizationMessage();
            authorizationMessage.setAccessToken(jwt.generateAccess(email));
            authorizationMessage.setAccessToken(jwt.generateRefresh(email));

            return new ProducerRecord<>(Topic.AUTHORIZATION.name(), email, JsonTools.toJson(authorizationMessage));
        }

        return null;
    }
}
