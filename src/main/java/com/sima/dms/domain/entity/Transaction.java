package com.sima.dms.domain.entity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DMS_TRANSACTION")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRANSACTION_CODE",unique = true)
    private String transactionCode;

    @Column(name = "DOCUMENT_CLASS")
    private String documentClass;

    @Column(name = "SUBSYSTEM_NAME")
    private String subsystemName;

    @Column(name = "DESCRIPTION")
    private String description;

}
