package koh.service.auth.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResponseCode {
    INVALID_EMAIL(10),
    CREATED_USER(21),
    EXISTED_USER(22),
    NOT_EXISTED_USER(23),
    AUTH_USER(20),
    BAD_REQUEST(-1);

    public final int code;
}
