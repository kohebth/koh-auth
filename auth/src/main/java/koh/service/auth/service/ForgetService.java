package koh.service.auth.service;

import koh.core.base.AbstractService;
import koh.core.base.HttpBody;
import koh.core.base.HttpParameter;
import koh.core.base.SimpleEnvelope;
import koh.core.base.impl.EmptyParameter;
import koh.service.auth.common.AuthCookie;
import koh.service.auth.common.AuthHeader;

public class ForgetService extends AbstractService {

    public ForgetService() {
        super(EmptyParameter.class, AuthHeader.class, AuthCookie.class, ForgetUserRequestBody.class);
    }

    @Override
    public SimpleEnvelope handle(HttpParameter parameter, SimpleEnvelope envelope) {
        ForgetUserRequestBody payload = envelope.getBody();
        ForgetUserResponseBody response = new ForgetUserResponseBody();


        return null;
    }

    static class ForgetUserRequestBody extends HttpBody {
        public String email;
    }

    static class ForgetUserResponseBody {
        public int code;
        public String message;
    }
}
