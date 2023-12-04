package com.sima.dms.domain.entity.baseinformation;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DMS_FILE_STATUS") //, uniqueConstraints = @UniqueConstraint(columnNames = {"FILE_TYPE_ID", "IS_DEFAULT"})
public class FileStatus {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @ManyToOne
    @JoinColumn(name = "FILE_TYPE_ID")
    private FileType fileType;

    @Column(name = "IS_DEFAULT")
    private Boolean isDefault = false;

    @Column(name = "BASIC")
    private Boolean basic=false;
}
