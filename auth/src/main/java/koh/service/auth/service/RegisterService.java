package koh.service.auth.service;

import koh.core.base.AbstractService;
import koh.core.base.HttpBody;
import koh.core.base.HttpParameter;
import koh.core.base.SimpleEnvelope;
import koh.core.base.impl.EmptyParameter;
import koh.core.helper.EnvelopeTools;
import koh.db.hub.repository.UserRepository;
import koh.db.hub.vps_management.tables.records.UserRecord;
import koh.service.auth.common.AuthCookie;
import koh.service.auth.common.AuthHeader;
import koh.service.auth.common.ResponseCode;
import koh.service.auth.tools.Password;
import koh.service.auth.tools.Validators;

import java.util.Optional;

public class RegisterService extends AbstractService {
    Validators validators = new Validators();

    public RegisterService() {
        super(EmptyParameter.class, AuthHeader.class, AuthCookie.class, RegisterRequestBody.class);
    }

    @Override
    public SimpleEnvelope handle(HttpParameter parameter, SimpleEnvelope envelope) {
        RegisterRequestBody payload = envelope.getBody();
        RegisterResponseBody response = new RegisterResponseBody();

        if (!validators.isSchoolEmail(payload.email)) {
            response.message = "Please, enter your school email!`";
            response.code = ResponseCode.INVALID_EMAIL.code;
            return EnvelopeTools.make(response, null, null);
        }
        String encryptedPassword = new Password(payload.password).getSecuredDigest();
        Optional<UserRecord> user = new UserRepository().createUser(payload.email, encryptedPassword);

        if (user.isPresent()) {
//            sendEmailService.sendAccessTokenEmail(user.get());

            response.code = ResponseCode.CREATED_USER.code;
            response.message = "User is registered successfully";
        }
        return null;
    }

    static class RegisterRequestBody extends HttpBody {
        public String email;
        public String password;
    }

    static class RegisterResponseBody extends HttpBody {
        public int code = ResponseCode.BAD_REQUEST.code;
        public String message = ResponseCode.BAD_REQUEST.name();
    }
}
