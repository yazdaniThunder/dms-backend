package com.sima.dms.domain.entity.session;

import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.entity.Profile;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@Table(name = "DMS_REFRESH_TOKEN")
public class RefreshToken implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String code;

    private LocalDateTime expiresIn;

    private Boolean available = true;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public RefreshToken(Profile profile, Integer hoursToExpire) {
        this.profile = profile;
        this.expiresIn = now().plusHours(hoursToExpire);
        this.code = randomUUID().toString();
    }

    public RefreshToken(ProfileDto profileDto, Integer hoursToExpire) {
        this.expiresIn = now().plusHours(hoursToExpire);
        this.code = randomUUID().toString();
        this.profile = Optional.ofNullable(profileDto.getId()).map(Profile::new).orElse(null);
    }

    public Boolean nonExpired() {
        return expiresIn.isAfter(now());
    }
}
