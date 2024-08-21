package koh.service.auth.tools;

import javax.crypto.KeyGenerator;
import java.util.UUID;
import java.util.regex.Pattern;

public class Tools {
    private static final Pattern SIS_EMAIL_PATTERN = Pattern.compile("[a-z]+[.][a-z]+[0-9]{6}@sis[.]hust[.]edu[.]vn");

    public static boolean isSchoolEmail(String email) {
        return SIS_EMAIL_PATTERN.matcher(email).matches();
    }

    public static String generateSessionKey() {
        // Generate a secure session key
        return UUID.randomUUID().toString();
    }
}
