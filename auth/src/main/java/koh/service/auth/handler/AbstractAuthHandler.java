package koh.service.auth.handler;

import koh.service.auth.secure.Jwt;

public abstract class AbstractAuthHandler {
    final Jwt jwt;

    protected AbstractAuthHandler(Jwt jwt) {
        this.jwt = jwt;
    }
}
