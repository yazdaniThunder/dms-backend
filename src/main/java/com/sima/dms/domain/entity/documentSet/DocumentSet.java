package com.sima.dms.domain.entity.documentSet;

import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.baseinformation.FileStatus;
import com.sima.dms.domain.entity.baseinformation.FileType;
import com.sima.dms.domain.entity.baseinformation.OtherDocumentType;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "DMS_DOCUMENT_SET", uniqueConstraints = @UniqueConstraint(columnNames = {"ROWS_NUMBER", "SEQUENCE"}))
public class DocumentSet extends Auditable {

    @NotNull
    @Column(name = "FROM_DATE", nullable = false)
    private Instant fromDate;

    @NotNull
    @Column(name = "TO_DATE", nullable = false)
    private Instant toDate;

    @Column(name = "ROWS_NUMBER")
    private String rowsNumber;

    @Column(name = "SEQUENCE")
    private String sequence;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentSetTypeEnum type;

    @Column(name = "description")
    private String description;

    @Column(name = "ocr")
    private Boolean ocr = false;

    @Transient
    private Boolean haveConflict = false;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_start")
    private Instant uploadStart;

    @Column(name = "upload_end")
    private Instant uploadEnd;

    @Column(name = "OCR_FINISHED_TIME")
    private Instant ocrFinishedTime;

    @Column(name = "FILE_NUMBER")
    private String fileNumber;

    @Column(name = "CUSTOMER_NUMBER")
    private String customerNumber;

    @Formula("(select count(1) from DMS_DOCUMENT d where d.document_set_id = id)")
    private Long documentSize;

    @Formula("( select count(1) from DMS_DOCUMENT d inner join DMS_DOCUMENT_STATE ds on d.state_id = ds.id " +
            " where d.document_set_id = id and ds.STATUS = 0 )")
    private Long documentNotCheckedSize;

    @Transient
    private DocumentSetState firstState;

    @ManyToOne
    @JoinColumn(name = "FILE_STATUS_ID")
    private FileStatus fileStatus;

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DocumentSetState state;

    @ManyToOne
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @OneToMany(mappedBy = "documentSet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DocumentSetState> states = new ArrayList<>();

    @OneToMany(mappedBy = "documentSet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DocumentSetConflict> conflicts = new ArrayList<>();


    public void setConflicts(List<DocumentSetConflict> conflicts) {
        this.conflicts.clear();
        if (conflicts != null) {
            conflicts.forEach(conflict -> conflict.setDocumentSet(this));
            this.conflicts.addAll(conflicts);
        }
    }

    public void setStates(List<DocumentSetState> states) {
        this.states.clear();
        if (states != null) {
            states.forEach(conflict -> conflict.setDocumentSet(this));
            this.states.addAll(states);
        }
    }

    public void addStates(List<DocumentSetState> states) {
        if (states != null) {
            states.forEach(conflict -> conflict.setDocumentSet(this));
            this.states.addAll(states);
        }
    }

    public Boolean getHaveConflict() {
        if (this.conflicts.stream().anyMatch(c -> c.getResolver() == null))
            return true;
        else return false;
    }

    public DocumentSetState getFirstState() {
        if (this.states != null && !this.states.isEmpty())
            return this.states.stream().filter(state -> state.getLastState() == null).findFirst().get();
        return null;
    }

    public void setOcr(Boolean ocr) {
        if (ocr != null)
            this.ocr = ocr;
    }
}
