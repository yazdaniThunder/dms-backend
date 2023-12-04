package com.sima.dms.domain.entity.baseinformation;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DMS_OTHER_DOCUMENT_TYPE")
public class OtherDocumentType {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @ManyToOne
    @JoinColumn(name = "FILE_TYPE_ID")
    private FileType fileType;

    @Column(name = "BASIC")
    private Boolean basic=false;

}
