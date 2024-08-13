package koh.service.auth.common;

import koh.core.base.HttpCookie;
import lombok.Getter;

@Getter
public class AuthCookie extends HttpCookie {
    Cookie sid;
    Cookie uid;
}
