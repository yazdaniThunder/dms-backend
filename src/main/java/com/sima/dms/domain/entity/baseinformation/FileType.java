package com.sima.dms.domain.entity.baseinformation;

import com.sima.dms.domain.entity.common.Auditable;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "DMS_FILE_TYPE")
public class FileType extends Auditable {

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ACTIVATE_FILE_NUMBER")
    private Boolean activateFileNumber = true;

    @OneToMany(mappedBy = "fileType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FileStatus> fileStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "fileType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OtherDocumentType> otherDocumentTypes = new ArrayList<>();


    public void setFileStatuses(List<FileStatus> fileStatuses) {
        this.fileStatuses.clear();
        if (fileStatuses != null) {
            fileStatuses.forEach(fileStatus -> fileStatus.setFileType(this));
            this.fileStatuses.addAll(fileStatuses);
        }
    }

    public void setOtherDocumentTypes(List<OtherDocumentType> otherDocumentTypes) {
        this.otherDocumentTypes.clear();
        if (otherDocumentTypes != null) {
            otherDocumentTypes.forEach(documentType -> documentType.setFileType(this));
            this.otherDocumentTypes.addAll(otherDocumentTypes);
        }
    }
}
