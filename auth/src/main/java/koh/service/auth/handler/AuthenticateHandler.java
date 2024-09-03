package koh.service.auth.handler;

import koh.db.hub.metadata.MetaEnv;
import koh.db.hub.repository.UserMetadataRepository;
import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.enums.UserMetadataType;
import koh.db.hub.vps_management.tables.records.UserMetadataRecord;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.kafka.message.AuthenticationMessage;
import koh.service.auth.kafka.message.AuthorizationMessage;
import koh.service.auth.kafka.message.StatusMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.secure.Password;
import koh.service.auth.tools.JsonTools;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

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

        try {
            UserRecord user = findUser(email, encryptedPassword);
            MetaEnv metaEnv = findMetaEnv(email);

            if (!metaEnv.getActivation()) {
                throw new AuthenticationException("Your account is not activated! Check your email for confirmation.");
            }
            Long userId = user.getId();
            String userEmail = user.getEmail();
            AuthorizationMessage authorizationMessage = new AuthorizationMessage();
            authorizationMessage.setAccessToken(jwt.generateAccess(userEmail, userId));
            authorizationMessage.setRefreshToken(jwt.generateRefresh(userEmail, userId));

            bus.respond(TOPIC_AUTH_LOGIN_RESPONSE, requestId, authorizationMessage);
        } catch (AuthenticationException e) {
            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus(e.getMessage());
            bus.respond(TOPIC_AUTH_LOGIN_RESPONSE, requestId, statusMessage);
        }
    }

    @NonNull
    UserRecord findUser(String email, String password) {
        return new UserRepository()
                .getUserByEmailAndPassword(email, password)
                .orElseThrow(() -> new AuthenticationException("Email or password is incorrect!"));
    }

    @NonNull
    MetaEnv findMetaEnv(String email) {
        return new UserMetadataRepository()
                .getMetadata(email, UserMetadataType.ENVIRONMENT)
                .map(UserMetadataRecord::getBlob)
                .map(b -> {
                    try {
                        return JsonTools.fromJson(b, MetaEnv.class);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .orElseThrow(() -> new AuthenticationException("Invalid state account!"));
    }

    static class AuthenticationException extends RuntimeException {
        AuthenticationException(String message) {
            super(message);
        }
    }
}
