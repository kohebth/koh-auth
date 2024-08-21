package koh.service.auth.message;

import lombok.Data;

@Data
public class ConfirmationEmailMessage {
    String userEmail;
    String userTemporaryToken;
}
