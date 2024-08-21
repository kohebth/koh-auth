package koh.service.auth.message;

import lombok.Data;

@Data
public class RegisterMessage {
    String email;
    String password;
}
