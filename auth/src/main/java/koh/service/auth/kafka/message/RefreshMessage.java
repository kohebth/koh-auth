package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class RefreshMessage {
    String email;
    String refreshToken;
}
