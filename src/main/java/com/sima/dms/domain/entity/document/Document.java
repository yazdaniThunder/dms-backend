package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.NodeDocument;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.entity.documentSet.DocumentSet;
import com.sima.dms.domain.enums.BIStatusEnum;
import com.sima.dms.domain.enums.ProcessStateEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "DMS_DOCUMENT")
public class Document extends Auditable {

    @Column(name = "NAME")
    private String name;

    @Column(name = "MAINTENANCE_CODE")
    private String maintenanceCode;

    @Column(name = "CONVERT_FLAG")
    private boolean convertFlag = false;

    @Transient
    private boolean haveConflict = false;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    private ProcessStateEnum processStateEnum;

    @Column(name = "BI_STATUS")
    @Enumerated(EnumType.STRING)
    private BIStatusEnum biStatus;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "BI_DESCRIPTION")
    private String biDescription;

    @Column(name = "OCR_PROCESS_TIME")
    private Long ocrProcessTime;

    @Column(name = "BI_PROCESS_TIME")
    private Long biProcessTime;

    @ManyToOne
    private Profile primaryApprover;

    @ManyToOne(cascade = CascadeType.ALL)
    private NodeDocument file;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "DOCUMENT_SET_ID")
    private DocumentSet documentSet;

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DocumentState state;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DocumentState> states = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DocumentConflict> conflicts = new ArrayList<>();

    public void setConflicts(List<DocumentConflict> conflicts) {
        this.conflicts.clear();
        if (conflicts != null) {
            conflicts.forEach(conflict -> conflict.setDocument(this));
            this.conflicts.addAll(conflicts);
        }
    }

    public void setStates(List<DocumentState> states) {
        this.states.clear();
        if (states != null) {
            states.forEach(conflict -> conflict.setDocument(this));
            this.states.addAll(states);
        }
    }

    public void addStates(List<DocumentState> states) {
        if (states != null) {
            states.forEach(conflict -> conflict.setDocument(this));
            this.states.addAll(states);
        }
    }

    public Boolean getHaveConflict() {
        if (this.conflicts.stream().anyMatch(c -> c.getResolver() == null))
            return true;
        else return false;
    }

}
