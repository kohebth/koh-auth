package koh.service.auth.message;

import lombok.Data;

@Data
public class RefreshMessage {
    String email;
    String refreshToken;
}
