package koh.service.auth;

import koh.core.server.SimpleServer;
import koh.service.auth.service.*;
import lombok.extern.slf4j.Slf4j;

import static koh.core.base.HttpMethod.*;

@Slf4j
public class App extends SimpleServer {
    public static void main(String[] args) {
        new App().start();
    }

    @Override
    protected void config() {
        host("0.0.0.0");
        port(8080);

        route(DELETE, "/session", SessionEndService.class);
        route(POST, "/session", SessionBeginService.class);
        route(GET, "/session", SessionAuthenticateService.class);
        route(PATCH, "/forget", ForgetService.class);
        route(POST, "/register", RegisterService.class);
    }
}
