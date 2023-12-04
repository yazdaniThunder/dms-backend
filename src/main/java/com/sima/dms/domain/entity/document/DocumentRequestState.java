package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.DocumentRequestStateEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DMS_DOCUMENT_REQUEST_STATE")
public class DocumentRequestState extends Auditable {

    @Column(name = "STATE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentRequestStateEnum state;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "DOCUMENT_REQUEST_ID")
    private DocumentRequest documentRequest;


    @ManyToMany
    @JoinTable(name = "DMS_DOCUMENT_REQUEST_STATE_SEEN",
            joinColumns = {@JoinColumn(name = "DOCUMENT_REQUEST_STATE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PROFILE_ID")})
    private List<Profile> profileSeen = new ArrayList<>();

    public DocumentRequestState(DocumentRequest documentRequest, DocumentRequestStateEnum state, String description) {
        this.state = state;
        this.description = description;
        this.documentRequest = documentRequest;
    }

    public DocumentRequestState(DocumentRequest documentRequest, DocumentRequestStateEnum state) {
        this.state = state;
        this.documentRequest = documentRequest;
    }
}
