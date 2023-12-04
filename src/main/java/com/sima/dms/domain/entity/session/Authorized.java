package com.sima.dms.domain.entity.session;

import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.service.impl.session.SessionService;
import com.sima.dms.utils.Responses;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;

import static com.sima.dms.utils.Responses.unauthorized;

public class Authorized extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String firstName;
    private final String lastName;

    public Authorized(Long id, RoleEnum authorities) {
        super("USERNAME", "SECRET", Collections.singleton(authorities));
        this.id = id;
        this.firstName = "";
        this.lastName = "";
    }

    public Authorized(Profile profile) {
        super(
                profile.getUser().getPersonelUserName(),
                null,
                profile.isActive(),
                true,
                true,
                true,
                Arrays.asList(profile.getRole())

        );
        this.id = profile.getId();
        this.firstName = profile.getUser().getFirstName();
        this.lastName = profile.getUser().getLastName();
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UsernamePasswordAuthenticationToken getAuthentication() {
        return new UsernamePasswordAuthenticationToken(this, null, getAuthorities());
    }

    public Boolean isAdmin() {
        return getAuthorities()
                .stream()
                .anyMatch((role) -> role.getAuthority().equals(RoleEnum.ADMIN.toString()));
    }

    public Boolean isBranchUser() {
        return getAuthorities()
                .stream()
                .anyMatch((role) -> role.getAuthority().equals(RoleEnum.BU.toString()));
    }

    public Boolean isBranchAdmin() {
        return getAuthorities()
                .stream()
                .anyMatch((role) -> role.getAuthority().equals(RoleEnum.BA.toString()));
    }

    public Boolean isDocumentOfficeAdmin() {
        return getAuthorities()
                .stream()
                .anyMatch((role) -> role.getAuthority().equals(RoleEnum.DOA.toString()));
    }

    public Boolean itsMe(Long id) {
        return getId().equals(id);
    }

    public Boolean itsBranchUserOrAdmin() {
        if (isAdmin() || isBranchUser())
            return true;
        return false;
    }

    public Boolean itsMeOrSessionIsADM(Long id) {
        Boolean admin = isAdmin();
        Boolean equals = getId().equals(id);
        if (admin) {
            return true;
        }
        return equals;
    }

    public static Long currentUserId() {
        return SessionService.authorized().orElseThrow(() -> unauthorized("Not authorized.")).getId();
    }

    public static Profile currentUser() {
        return new Profile(SessionService.authorized().orElseThrow(() -> unauthorized("Not authorized.")).getId());
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}