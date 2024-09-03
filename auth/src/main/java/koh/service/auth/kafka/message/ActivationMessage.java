package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class ActivationMessage {
    String userEmail;
    String userTemporaryToken;
}
