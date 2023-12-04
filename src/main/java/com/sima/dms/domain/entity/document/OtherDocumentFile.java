package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.NodeDocument;
import com.sima.dms.domain.entity.baseinformation.FileStatus;
import com.sima.dms.domain.entity.baseinformation.OtherDocumentType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DMS_OTHER_DOCUMENT_FILE")
@NoArgsConstructor
public class OtherDocumentFile {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @JoinColumn(name ="FILE_UUID")
    @OneToOne
    private NodeDocument file;

    @ManyToOne
    @JoinColumn(name = "FILE_STATUS_ID")
    private FileStatus fileStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OTHER_DOCUMENT_TYPE_ID")
    private OtherDocumentType otherDocumentType;

    @ManyToOne
    @JoinColumn(name = "OTHER_DOCUMENT_ID")
    private OtherDocument otherDocument;

    public OtherDocumentFile( OtherDocument otherDocument , OtherDocumentType otherDocumentType) {
        this.otherDocumentType = otherDocumentType;
        this.otherDocument = otherDocument;
    }
}
