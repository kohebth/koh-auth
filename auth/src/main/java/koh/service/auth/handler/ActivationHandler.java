package koh.service.auth.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import koh.db.hub.metadata.MetaEnv;
import koh.db.hub.repository.UserMetadataRepository;
import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.enums.UserMetadataType;
import koh.db.hub.vps_management.tables.records.UserMetadataRecord;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.kafka.message.ActivationMessage;
import koh.service.auth.kafka.message.StatusMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

import static koh.service.auth.kafka.KafkaRespTopic.TOPIC_AUTH_ACTIVATION_RESPONSE;

@Slf4j
public class ActivationHandler extends AbstractAuthHandler implements MessageHandler {

    public ActivationHandler(Jwt jwt, KafkaProducerWorker response) {
        super(jwt, response);
    }

    @Override
    public void handle(ConsumerRecord<String, String> message)
            throws IOException {

        String requestId = message.key();

        ActivationMessage m = JsonTools.fromJson(message.value(), ActivationMessage.class);

        try {
            String token = m.getUserTemporaryToken();
            Claims c = jwt.verify(token);
            Long userId = Long.parseLong(c.getId());
            String email = c.getSubject();
            checkUserExist(userId, email);
            activateUser(getUserMetadata(email));

            bus.respond(TOPIC_AUTH_ACTIVATION_RESPONSE, requestId, "Your account has been activated!");
        } catch (ActivationException e) {
            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus(e.getMessage());
            bus.respond(TOPIC_AUTH_ACTIVATION_RESPONSE, requestId, statusMessage);
        } catch (JwtException e) {
            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus("Activation timed out!");
            bus.respond(TOPIC_AUTH_ACTIVATION_RESPONSE, requestId, statusMessage);
        }
    }

    void checkUserExist(Long userId, String email) {
        Long id = new UserRepository()
                .getUserByEmail(email)
                .map(UserRecord::getId)
                .orElseThrow(() -> new ActivationException("Invalid activation request!"));
        if (!userId.equals(id)) {
            throw new ActivationException("Broken request token!");
        }
    }

    UserMetadataRecord getUserMetadata(String email) {
        return new UserMetadataRepository()
                .getMetadata(email, UserMetadataType.ENVIRONMENT)
                .orElseThrow(() -> new ActivationException("User is not found!"));
    }

    void activateUser(UserMetadataRecord metadata) {
        try {
            MetaEnv metaEnv = JsonTools.fromJson(metadata.getBlob(), MetaEnv.class);
            metaEnv.setActivation(true);
            new UserMetadataRepository()
                    .updateMetadata(metadata.getId(), JsonTools.toJsonBytes(metaEnv))
                    .orElseThrow(() -> new ActivationException(""));
        } catch (Exception e) {
            throw new ActivationException("Unable to activate!");
        }
    }

    static class ActivationException extends RuntimeException {
        ActivationException(String message) {
            super(message);
        }
    }
}
