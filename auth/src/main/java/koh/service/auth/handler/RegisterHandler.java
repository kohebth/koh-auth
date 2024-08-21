package koh.service.auth.handler;

import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.kafka.Topic;
import koh.service.auth.message.ConfirmationEmailMessage;
import koh.service.auth.message.RegisterMessage;
import koh.service.auth.secure.Jwt;
import koh.service.auth.secure.Password;
import koh.service.auth.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class RegisterHandler extends AbstractAuthHandler implements MessageHandler {
    public RegisterHandler(Jwt jwt) {
        super(jwt);
    }

    @Override
    public ProducerRecord<String, String> handle(ConsumerRecord<String, String> message)
            throws IOException {
        RegisterMessage m = JsonTools.fromJson(message.value(), RegisterMessage.class);

        String email = m.getEmail();
        String password = m.getPassword();

        String encryptedPassword = new Password(password).getSecuredDigest();

        Optional<UserRecord> existedUser = new UserRepository().getUserByEmail(email);
        if (existedUser.isPresent()) {
            return null;
        }
        Optional<UserRecord> user = new UserRepository().createUser(email, encryptedPassword);

        if (user.isPresent()) {
            ConfirmationEmailMessage confirmationMessage = new ConfirmationEmailMessage();
            confirmationMessage.setUserEmail(email);
            log.info("POSSIBLE");
            confirmationMessage.setUserTemporaryToken(jwt.generateAccess(email));
            log.info("Access key generated to email = {}", email);
            return new ProducerRecord<>(Topic.CONFIRMATION_EMAIL.name(), email, JsonTools.toJson(confirmationMessage));
        }

        return null;
    }
}
