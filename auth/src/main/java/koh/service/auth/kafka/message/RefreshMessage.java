package koh.service.auth.kafka.message;

import lombok.Data;

@Data
public class RefreshMessage {
    Long userId;
    String email;
    String refreshToken;
}
