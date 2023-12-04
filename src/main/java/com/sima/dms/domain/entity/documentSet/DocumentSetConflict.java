package com.sima.dms.domain.entity.documentSet;

import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "DMS_DOCUMENT_SET_CONFLICT")
public class DocumentSetConflict extends Auditable{

    @ManyToOne
    private Profile resolver;

    @Column(name = "RESOLVING_DATE")
    private Instant resolvingDate;

    @Column(name = "REGISTER_DESCRIPTION",length = 2000)
    private String registerDescription;

    @Column(name = "RESOLVING_DESCRIPTION",length = 2000)
    private String resolveDescription;

    @ManyToOne
    private DocumentSet documentSet;

    @ManyToMany
    @JoinTable(name = "DMS_DSC_REASON",
            joinColumns = {@JoinColumn(name = "DSC_ID")},
            inverseJoinColumns = {@JoinColumn(name = "REASON_ID")})
    private List<ConflictReason> conflictReasons = new ArrayList<>();

}
