package com.sima.dms.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DMS_DOCUMENT_TYPE")
public class DocumentType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NUMBERS", length = 255)
    private String numbers;

    @Column(name = "TITLE", length = 255)
    private String title;

    @Column(name = "SIMILAR_TITLE", length = 255)
    private String similarTitle;

    @Column(name = "SCORE")
    private Integer score = 0;

}