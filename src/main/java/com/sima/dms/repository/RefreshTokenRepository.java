package com.sima.dms.repository;

import com.sima.dms.domain.entity.session.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@SuppressWarnings("unused")
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Transactional
    @Modifying
    @Query(" UPDATE RefreshToken refresh SET refresh.available = false\n" +
            "WHERE refresh.profile.id = ?1 AND refresh.available = true")
    void disableOldRefreshTokens(Long id);

    @Query("SELECT refresh FROM RefreshToken refresh\n" +
            "JOIN FETCH refresh.profile user\n" +
            "WHERE refresh.code = ?1 AND refresh.available = true")
    Optional<RefreshToken> findOptionalByCodeAndAvailableIsTrue(String code);

}
