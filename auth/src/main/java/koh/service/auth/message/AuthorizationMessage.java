package koh.service.auth.message;

import lombok.Data;

@Data
public class AuthorizationMessage {
    String refreshToken;
    String accessToken;
}
