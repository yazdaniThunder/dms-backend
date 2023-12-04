package com.sima.dms.domain.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.session.Authorized;
import com.sima.dms.service.impl.session.SessionService;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;

@Data
@MappedSuperclass
public abstract class Auditable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @JsonIgnore
    @CreatedDate
    @Column(name = "register_date")
    protected Instant registerDate;

    @JsonIgnore
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @CreatedBy
    @JoinColumn(name = "created_by")
    @ManyToOne(fetch = LAZY)
    protected Profile createdBy;

    @LastModifiedBy
    @JoinColumn(name = "last_modified_by")
    @ManyToOne(fetch = LAZY)
    protected Profile lastModifiedBy;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @JsonIgnore
    public Profile getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public Profile getUpdatedBy() {
        return lastModifiedBy;
    }

    public Boolean isActive() {
        return this.active;
    }

    @PrePersist
    private void save() {
        registerDate = Instant.now();
        SessionService.authorized().ifPresent(authorized -> {
                    if (this.createdBy == null)
                        createdBy = Authorized.currentUser();
                }
        );
    }

    @PreUpdate
    private void update() {
        lastModifiedDate = Instant.now();
        SessionService.authorized().ifPresent(authorized -> lastModifiedBy = Authorized.currentUser());
    }
}