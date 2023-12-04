package com.sima.dms.domain.entity.document;


import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import lombok.Data;
import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "DMS_DOCUMENT_CONFLICT")
public class DocumentConflict extends Auditable{

    @Column(name = "SEND_DATE")
    private Instant sendDate;

    @Column(name = "RESOLVING_DATE")
    private Instant resolvingDate;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    private Profile sender;

    @ManyToOne
    @JoinColumn(name = "RESOLVER_ID")
    private Profile resolver;

    @Column(name = "REGISTER_DESCRIPTION",length = 2000)
    private String registerDescription;

    @Column(name = "RESOLVING_DESCRIPTION",length = 2000)
    private String resolveDescription;

    @ManyToOne
    @JoinColumn(name = "DOCUMENT_INFO_ID")
    private Document document;

    @ManyToMany
    @JoinTable(name = "DMS_DC_REASON",
            joinColumns = {@JoinColumn(name = "DC_ID")},
            inverseJoinColumns = {@JoinColumn(name = "REASON_ID")})
    private List<ConflictReason> conflictReasons = new ArrayList<>();

}
