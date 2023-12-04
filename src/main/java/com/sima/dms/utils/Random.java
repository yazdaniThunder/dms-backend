package com.sima.dms.utils;

import com.sima.dms.domain.enums.RoleEnum;

import java.time.LocalDateTime;

import static com.sima.dms.constants.Security.JWT;
import static com.sima.dms.constants.Security.TOKEN_SECRET;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

public class Random {

    private static final java.util.Random RANDOM = new java.util.Random();

    public static Integer between(Integer min, Integer max) {
        return RANDOM.nextInt(max - min) + min;
    }

    public static String code() {
        return format("%s%s%s%s", between(0, 9), between(0, 9), between(0, 9), between(0, 9));
    }

    public static String token(Long id, RoleEnum roles) {
        return token(id, now().plusHours(24), TOKEN_SECRET, roles);
    }

    public static String token(RoleEnum roles) {
        return token(between(1, 9999).longValue(), now().plusHours(24), TOKEN_SECRET, roles);
    }

    public static String token(RoleEnum roles, String secret) {
        return token(between(1, 9999).longValue(), now().plusHours(24), secret, roles);
    }

    public static String token(LocalDateTime expiration, RoleEnum roles) {
        return token(between(1, 9999).longValue(), expiration, TOKEN_SECRET, roles);
    }

    public static String token(LocalDateTime expiration, RoleEnum roles, String secret) {
        return token(between(1, 9999).longValue(), expiration, secret, roles);
    }

    public static String token(Long id, LocalDateTime expiration, String secret, RoleEnum roles) {
        String token = JWT.encode(id, roles, expiration, secret);

        return format("Bearer %s", token);
    }
}
