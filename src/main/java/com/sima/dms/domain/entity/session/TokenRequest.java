package com.sima.dms.domain.entity.session;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Getter
@Setter
public class TokenRequest {

    @NotEmpty(message = "{token.email.not-null}")
    //@Email(message = "{token.email.is-valid}")
    private String username;

    @NotEmpty(message = "{token.password.not-null}")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenRequest)) return false;
        return getUsername().equals(getUsername()) && getPassword().equals(getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword());
    }
}
