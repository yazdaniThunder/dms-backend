package com.sima.dms.domain.entity.document;

import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.NodeDocument;
import com.sima.dms.domain.entity.baseinformation.DocumentRequestReason;
import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.DocumentRequestTypeEnum;
import com.sima.dms.domain.enums.RequestTypeEnum;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "DMS_DOCUMENT_REQUEST")
public class DocumentRequest extends Auditable {

    @Column(name = "TYPE",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private DocumentRequestTypeEnum documentType;

    @Column(name = "REQUEST_TYPE",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RequestTypeEnum requestType;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "DOCUMENT_DATE")
    private Instant documentDate;

    @Column(name = "DOCUMENT_DATE_FROM")
    private Instant documentDateFrom;

    @Column(name = "DOCUMENT_DATE_To")
    private Instant documentDateTo;

    @Column(name = "DOCUMENT_AMOUNT")
    private Long documentAmount;

    @Column(name = "CUSTOMER_NUMBER")
    private String customerNumber;

    @Column(name = "FILE_NUMBER")
    private String fileNumber;

    @Column(name = "FILE_TITLE")
    private String fileTitle;

    @Column(name = "FILE_DATE")
    private Instant fileDate;

    @Column(name = "FILE_DATE_FROM")
    private Instant fileDateFrom;

    @Column(name = "FILE_DATE_To")
    private Instant fileDateTo;

    @Column(name = "SENT_DATE")
    private Instant sentDate;

    @Column(name = "EXPIRY_DATE")
    private Instant expiryDate;

    @Column(name = "REQUEST_DESCRIPTION")
    private String requestDescription;

    @Column(name = "RECEIVE_DESCRIPTION")
    private String receiveDescription;

    @Column(name = "REFERENCE_TITLE")
    private String referenceTitle;

    @Column(name = "CHECK_DATE")
    private Instant checkDate;

    @Column(name = "CHECK_NUMBER")
    private String checkNumber;

    @Column(name = "CHECK_RECEIPT_DATE")
    private Instant checkReceiptDate;

    @Column(name = "CHECK_RECEIPT_DATE_FROM")
    private Instant checkReceiptDateFrom;

    @Column(name = "CHECK_RECEIPT_DATE_TO")
    private Instant checkReceiptDateTo;

    @Column(name = "CHECK_ISSUING_BANK")
    private String checkIssuingBank;

    @OneToOne
    private NodeDocument branchFile;

    @ManyToMany
    @JoinTable(name = "DMS_DOCUMENT_REQUEST_OFFICE_FILE",
            joinColumns = {@JoinColumn(name = "DOCUMENT_REQUEST_ID")},
            inverseJoinColumns = {@JoinColumn(name = "UUID")})
    private List<NodeDocument> officeFiles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "REASON_ID")
    private DocumentRequestReason documentRequestReason;


    @ManyToOne
    @JoinColumn(name = "REQUEST_BRANCH_ID")
    private Branch requestBranch;

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DocumentRequestState lastState;
}
