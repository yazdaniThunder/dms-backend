package com.sima.dms.service.impl.session;

import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.session.Authorized;
import com.sima.dms.repository.ProfileRepository;
import com.sima.dms.utils.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.sima.dms.constants.Security.JWT;
import static com.sima.dms.constants.Security.TOKEN_SECRET;
import static com.sima.dms.utils.Responses.expired;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
@AllArgsConstructor
public class SessionService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Profile profile = profileRepository.findByUser_PersonelUserNameIgnoreCaseAndActiveIsTrue(username);
        return new Authorized(profile);
    }

    public static void authorize(HttpServletRequest request, HttpServletResponse response) {

        String token = Authorization.extract(request);
        if (isNull(token)) {
            return;
        }
        try {
            Authorized authorized = JWT.decode(token, TOKEN_SECRET);
            SecurityContextHolder.getContext().setAuthentication(authorized.getAuthentication());
        } catch (Exception exception) {
            expired(response);
        }

    }

    public static Optional<Authorized> authorized() {
        try {
            Object principal = getPrincipal();

            if (nonNull(principal) && principal instanceof Authorized) {
                return of((Authorized) principal);
            } else return empty();
        } catch (Exception exception) {
            return empty();
        }
    }

    private static Object getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (nonNull(authentication)) {
            return authentication.getPrincipal();
        }
        return null;
    }
}
