package com.sima.dms.constants;

import com.sima.dms.service.impl.session.JsonWebToken;
import com.sima.dms.shared.PublicRoutes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static com.sima.dms.shared.PublicRoutes.create;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class Security {

    public static final PublicRoutes PUBLICS = create()
            .add(GET, "/api")
            .add(POST, "/api/users", "/api/sessions/**", "/api/recoveries/**");

    public static final Integer DAY_MILLISECONDS = 86400;

    public static final JsonWebToken JWT = new JsonWebToken();

    public static final Integer PASSWORD_STRENGTH = 10;
    public static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(PASSWORD_STRENGTH);

    public static final String ROLES_KEY_ON_JWT = "role";

    public static String TOKEN_SECRET = "secret";
    public static Integer TOKEN_EXPIRATION_IN_MINUTES = 30;
    public static Integer REFRESH_TOKEN_EXPIRATION_IN_HOURS = 2;

    public static String SESSION_COOKIE_NAME;

    public static final String USERNAME_PARAMETER = "email";
    public static final String PASSWORD_PARAMETER = "password";

    public static final String HOME_URL = "/app";
    public static final String LOGIN_URL = "/app/login";
    public static final String LOGIN_ERROR_URL = LOGIN_URL + "?error=true";
    public static final String ACCESS_DENIED_URL = LOGIN_URL + "?denied=true";
    public static final String LOGOUT_URL = "/app/logout";

    public static final String SECURITY_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCEPTABLE_TOKEN_TYPE = SECURITY_TYPE + " ";
    public static final String CAN_T_WRITE_RESPONSE_ERROR = "can't write response error.";
    public static final Integer BEARER_WORD_LENGTH = SECURITY_TYPE.length();

}
