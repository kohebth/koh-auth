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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static koh.db.hub.vps_management.tables.Session.SESSION;

@Getter
public class SessionEndService extends AbstractService {
    private static final Logger log = LoggerFactory.getLogger(SessionEndService.class);

    public SessionEndService() {
        super(EmptyParameter.class, AuthHeader.class, AuthCookie.class, HttpBody.class);
    }

    @Override
    public SimpleEnvelope handle(HttpParameter parameter, SimpleEnvelope envelope) {
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


        } catch (Exception e) {
            log.error("", e);
        }
        return EnvelopeTools.make(EnvelopeTools.ACCEPTED_BODY, EnvelopeTools.EMPTY_HEADER, EnvelopeTools.EMPTY_COOKIE);
    }
}
