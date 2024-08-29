package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class AuthorizationMessage {
    String refreshToken;
    String accessToken;
}
