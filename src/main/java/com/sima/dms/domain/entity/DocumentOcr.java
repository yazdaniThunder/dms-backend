package com.sima.dms.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DMS_DOCUMENT_OCR")
public class DocumentOcr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "LEVENSHTEIN_DISTANCE")
    private double levenshteinDistance;

    @Column(name = "JAROWINKLER_DISTANCE")
    private double jaroWinklerDistance;

    @Column(name = "DATES")
    private String dates;

    @Column(name = "BRANCH_COEDS")
    private String branchCodes;

    @Column(name = "DOCUMENT_NUMBERS")
    private String documentNumbers;

    @Column(name = "TYPE_MATCHES")
    private boolean typeMatches;

    @Lob
    @Column(name = "OCR_TEXT")
    private String ocrText;

    @Lob
    @Column(name = "OCR_NUMBER")
    private String ocrNumber;

    @ManyToOne
    private NodeDocument nodeDocument;
}
