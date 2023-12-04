package com.sima.dms.service.impl.session;

import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.session.Authorized;
import com.sima.dms.domain.enums.RoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.time.LocalDateTime;

import static com.sima.dms.constants.Security.ROLES_KEY_ON_JWT;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.stream;
import static java.util.Date.from;

public class JsonWebToken {
    public String encode(Profile profile, LocalDateTime expiration, String secret) {
        return encode(profile.getId(), profile.getRole(), expiration, secret);
    }

    public String encode(ProfileDto profile, LocalDateTime expiration, String secret) {
        return encode(profile.getId(), profile.getRole(), expiration, secret);
    }

    public String encode(
            Long id,
            RoleEnum authorities,
            LocalDateTime expiration,
            String secret
    ) {
        return Jwts.builder()
                .setSubject(id.toString())
                .claim(ROLES_KEY_ON_JWT,  authorities)
                .setExpiration(from(expiration.atZone(systemDefault()).toInstant()))
                .signWith(HS256, secret)
                .compact();
    }

    public Authorized decode(String token, String secret) {

        Jws<Claims> decoded = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        Long id = Long.parseLong(decoded.getBody().getSubject());
        String joinedRolesString = decoded.getBody().get(ROLES_KEY_ON_JWT).toString();
        RoleEnum authorities = RoleEnum.valueOf(joinedRolesString);
        return new Authorized(id, authorities);
    }
}
