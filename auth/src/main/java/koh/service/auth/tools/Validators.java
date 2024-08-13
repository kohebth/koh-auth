package koh.service.auth.tools;

import java.util.regex.Pattern;

public class Validators {
    private static final Pattern SIS_EMAIL_PATTERN;

    static {
        SIS_EMAIL_PATTERN = Pattern.compile("[a-z]+[.][a-z]+[0-9]{6}@sis[.]hust[.]edu[.]vn");
    }

    public boolean isSchoolEmail(String email) {
        return SIS_EMAIL_PATTERN.matcher(email).matches();
    }
}
