package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class RegisterMessage {
    String email;
    String password;
}
