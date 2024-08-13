package koh.service.auth.service;

import koh.core.base.AbstractService;
import koh.core.base.HttpBody;
import koh.core.base.HttpParameter;
import koh.core.base.SimpleEnvelope;
import koh.core.helper.EnvelopeTools;
import koh.core.base.impl.EmptyParameter;
import koh.db.hub.EntityManager;
import koh.db.hub.vps_management.tables.records.SessionRecord;
import koh.service.auth.common.AuthCookie;
import koh.service.auth.common.AuthHeader;
import lombok.Getter;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;

import static koh.db.hub.vps_management.tables.Session.SESSION;

@Getter
public class SessionAuthenticateService extends AbstractService {
    public SessionAuthenticateService() {
        super(EmptyParameter.class, AuthHeader.class, AuthCookie.class, RequestBody.class);
    }

    @Override
    public SimpleEnvelope handle(HttpParameter parameter, SimpleEnvelope envelope) {
        ResponseBody responseBody = new ResponseBody();
        AuthCookie cookie = envelope.getCookie();

        try {
            Long uid = Long.parseLong(cookie.getUid().getValue());
            String sid = cookie.getSid().getValue();
            LocalDateTime sidExpireTime = cookie.getSid().getExpireTime();
            SessionRecord session = EntityManager
                    .useContext()
                    .selectFrom(SESSION)
                    .where(DSL.and(SESSION.USER_ID.eq(uid),
                            SESSION.SESSION_ID.eq(sid),
                            SESSION.EXPIRE_TIME.eq(sidExpireTime)
                    ))
                    .fetchOptional()
                    .orElseThrow();

            responseBody.message = "Authorized";
            responseBody.publicSessionId = session.getSessionId();
        } catch (Exception e) {
            responseBody.message = "Unauthorized";
        }
        return EnvelopeTools.make(responseBody, EnvelopeTools.EMPTY_HEADER, cookie);
    }

    private static class RequestBody extends HttpBody {
        public String path;
    }

    private static class ResponseBody extends HttpBody {
        public String message;
        public String publicSessionId;
    }
}
