package koh.service.auth.common;

import java.util.UUID;

public class SessionTools {
    public String generateSessionKey(String userId) {
        // Generate a secure session key
        return UUID.randomUUID().toString();
    }
}

