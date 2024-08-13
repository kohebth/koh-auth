package koh.service.auth.service;

import koh.core.base.*;
import koh.core.helper.EnvelopeTools;
import koh.core.base.impl.EmptyParameter;
import koh.service.auth.common.AuthCookie;
import koh.service.auth.common.AuthHeader;
import lombok.Getter;

@Getter
public class SessionBeginService extends AbstractService {
    public SessionBeginService() {
        super(EmptyParameter.class, AuthHeader.class, AuthCookie.class, EnvelopeTools.EMPTY_BODY.getClass());
    }

    @Override
    public SimpleEnvelope handle(HttpParameter parameter, SimpleEnvelope envelope) {
        return EnvelopeTools.make(new DummyBody(), EnvelopeTools.EMPTY_HEADER, EnvelopeTools.EMPTY_COOKIE);
    }

    static class DummyBody extends HttpBody {
        public int trace = 0;
    }
}
