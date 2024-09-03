package koh.service.auth.handler;

import koh.db.hub.metadata.MetaEnv;
import koh.db.hub.repository.UserMetadataRepository;
import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.enums.UserMetadataType;
import koh.db.hub.vps_management.tables.records.UserMetadataRecord;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.kafka.message.ActivationMessage;
import koh.service.auth.kafka.message.RegisterMessage;
import koh.service.auth.kafka.message.StatusMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.secure.Password;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

import static koh.service.auth.kafka.KafkaReqTopic.TOPIC_MAIL_REGISTRATION_REQUEST;
import static koh.service.auth.kafka.KafkaRespTopic.TOPIC_AUTH_REGISTER_RESPONSE;

@Slf4j
public class RegisterHandler extends AbstractAuthHandler implements MessageHandler {

    public RegisterHandler(Jwt jwt, KafkaProducerWorker response) {
        super(jwt, response);
    }

    @Override
    public void handle(ConsumerRecord<String, String> message)
            throws IOException {
        String requestId = message.key();

        RegisterMessage m = JsonTools.fromJson(message.value(), RegisterMessage.class);

        String email = m.getEmail();
        String password = m.getPassword();

        try {
            checkDuplicationUser(email);

            String encryptedPassword = new Password(password).getSecuredDigest();
            UserRecord user = createUser(email, encryptedPassword);
            UserMetadataRecord metadataRecord = createMetadata(user);

            bus.respond(TOPIC_AUTH_REGISTER_RESPONSE, requestId, successMessage(user));
            bus.request(TOPIC_MAIL_REGISTRATION_REQUEST, requestId, confirmationEmail(user));
        } catch (RegistrationException e) {
            bus.respond(TOPIC_AUTH_REGISTER_RESPONSE, requestId, errorMessage(e.getMessage()));
        }
    }

    void checkDuplicationUser(String email) {
        boolean doesExist = new UserRepository().getUserByEmail(email).isPresent();
        if (doesExist) {
            throw new RegistrationException("The email has been registered!");
        }
    }

    UserRecord createUser(String email, String password) {
        return new UserRepository()
                .createUser(email, password)
                .orElseThrow(() -> new RegistrationException("Failed to create user!"));
    }

    UserMetadataRecord createMetadata(UserRecord user)
            throws IOException {
        return new UserMetadataRepository().createMetadata(
                user.getId(),
                user.getEmail(),
                UserMetadataType.ENVIRONMENT,
                JsonTools.toJsonBytes(new MetaEnv()),
                false
        ).orElseThrow(() -> new RuntimeException("Failed to create user metadata"));
    }

    ActivationMessage confirmationEmail(UserRecord user) {
        Long userId = user.getId();
        String userEmail = user.getEmail();
        ActivationMessage m = new ActivationMessage();
        m.setUserEmail(userEmail);
        m.setUserTemporaryToken(jwt.generateAccess(userEmail, userId));
        return  m;
    }

    StatusMessage successMessage(UserRecord user) {
        StatusMessage m = new StatusMessage();
        m.setStatus("Access key generated to email " + user.getEmail());
        return m;
    }

    StatusMessage errorMessage(String message) {
        StatusMessage m = new StatusMessage();
        m.setStatus(message);
        return m;
    }

    static class RegistrationException extends RuntimeException {
        RegistrationException(String message) {
            super(message);
        }
    }
}
