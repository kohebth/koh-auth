package koh.service.auth.handler;

import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.KafkaProducerWorker;
import koh.service.auth.kafka.message.ConfirmationEmailMessage;
import koh.service.auth.kafka.message.RegisterMessage;
import koh.service.auth.kafka.message.StatusMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.secure.Password;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.Optional;

import static koh.service.auth.kafka.KafkaReqTopic.TOPIC_MAIL_REGISTER_CONFIRMATION_REQUEST;
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

        String encryptedPassword = new Password(password).getSecuredDigest();

        Optional<UserRecord> existedUser = new UserRepository().getUserByEmail(email);
        if (existedUser.isPresent()) {
            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus("Email is registered!");
            bus.respond(TOPIC_AUTH_REGISTER_RESPONSE, requestId, statusMessage);
            return;
        }

        Optional<UserRecord> user = new UserRepository().createUser(email, encryptedPassword);

        if (user.isPresent()) {
            ConfirmationEmailMessage confirmationMessage = new ConfirmationEmailMessage();
            confirmationMessage.setUserEmail(email);

            confirmationMessage.setUserTemporaryToken(jwt.generateAccess(email));

            StatusMessage statusMessage = new StatusMessage();
            statusMessage.setStatus("Access key generated to email " + email);

            bus.respond(TOPIC_AUTH_REGISTER_RESPONSE, requestId, statusMessage);

            bus.request(TOPIC_MAIL_REGISTER_CONFIRMATION_REQUEST, requestId, confirmationMessage);
        }
    }
}
