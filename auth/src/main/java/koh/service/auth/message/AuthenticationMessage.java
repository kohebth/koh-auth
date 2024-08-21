package koh.service.auth.message;

import lombok.Data;

@Data
public class AuthenticationMessage {
    String email;
    String password;
    String sessionId;
}
