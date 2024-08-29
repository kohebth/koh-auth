package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class AuthenticationMessage {
    String email;
    String password;
}
