package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.DocumentStateEnum;
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
@Table(name = "DMS_DOCUMENT_STATE")
public class DocumentState extends Auditable {

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private DocumentStateEnum name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    private Document document;

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private DocumentState lastState;

    @ManyToMany
    @JoinTable(name = "DMS_DOCUMENT_STATE_SEEN",
            joinColumns = {@JoinColumn(name = "STATE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PROFILE_ID")})
    private List<Profile> profileSeen = new ArrayList<>();

    public DocumentState(DocumentStateEnum name, DocumentState lastState, String description) {
        this.name = name;
        this.lastState = lastState;
        this.description = description;
    }
}
