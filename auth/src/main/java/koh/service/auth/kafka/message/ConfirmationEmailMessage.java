package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class ConfirmationEmailMessage {
    String userEmail;
    String userTemporaryToken;
}
