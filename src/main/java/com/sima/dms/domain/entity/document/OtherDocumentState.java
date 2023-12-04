package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.OtherDocumentStateEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "DMS_OTHER_DOCUMENT_STATE")
@NoArgsConstructor
public class OtherDocumentState extends Auditable {

    @Column(name = "STATE")
    @Enumerated(EnumType.ORDINAL)
    private OtherDocumentStateEnum state;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "OTHER_DOCUMENT_ID")
    private OtherDocument otherDocument;

    @ManyToMany
    @JoinTable(name = "DMS_OTHER_DOCUMENT_STATE_SEEN",
            joinColumns = {@JoinColumn(name = "FILE_OTHER_DOCUMENT_STATE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PROFILE_ID")})
    private List<Profile> profileSeen = new ArrayList<>();

    public OtherDocumentState(OtherDocumentStateEnum state, OtherDocument otherDocument,String description) {
        this.state = state;
        this.description = description;
        this.otherDocument = otherDocument;
    }

    public OtherDocumentState(OtherDocumentStateEnum state,  OtherDocument otherDocument) {
        this.state = state;
        this.otherDocument = otherDocument;
    }
}
