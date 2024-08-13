package koh.service.auth.tools;

import javax.crypto.KeyGenerator;
import java.util.Base64;

public class Tools {
    public static KeyGenerator AES128KeyGenerator() {
        try {
            return KeyGenerator.getInstance("AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Base64.Encoder base64Encoder() {
        return Base64.getEncoder();
    }
}
