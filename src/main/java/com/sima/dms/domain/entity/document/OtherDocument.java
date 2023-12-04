package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.baseinformation.FileType;
import com.sima.dms.domain.entity.common.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "DMS_OTHER_DOCUMENT")
@NoArgsConstructor
public class OtherDocument extends Auditable {

    @Column(name = "CUSTOMER_NUMBER",nullable = false)
    private String customerNumber;

    @Column(name = "FILE_NUMBER")
    private String fileNumber;

    @ManyToOne
    @JoinColumn(name = "FILE_Type_ID")
    private FileType fileType;

    @ManyToOne
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @JoinColumn(name = "LAST_STATE_ID", unique = true)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private OtherDocumentState lastState;


    @OneToMany(mappedBy = "otherDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OtherDocumentFile> otherDocumentFiles = new ArrayList<>();

    public void setOtherDocumentFiles(List<OtherDocumentFile> otherDocumentFiles) {
        this.otherDocumentFiles.clear();
        if (otherDocumentFiles != null) {
            otherDocumentFiles.forEach(otherDocumentFile -> otherDocumentFile.setOtherDocument(this));
            this.otherDocumentFiles.addAll(otherDocumentFiles);
        }
    }
}
