package com.sima.dms.domain.entity.documentSet;

import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "DMS_DOCUMENT_SET_STATE")
public class DocumentSetState extends Auditable {

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private DocumentSetStateEnum name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    private DocumentSet documentSet;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private DocumentSetState lastState;

    @ManyToMany
    @JoinTable(name = "DMS_DOCUMENT_SET_STATE_SEEN",
            joinColumns = {@JoinColumn(name = "STATE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PROFILE_ID")})
    private List<Profile> profileSeen = new ArrayList<>();

    public DocumentSetState(DocumentSetStateEnum name) {
        this.name = name;
    }

    public DocumentSetState(DocumentSetStateEnum name, DocumentSetState lastState, String description) {
        this.name = name;
        this.lastState = lastState;
        this.description = description;
    }
}
