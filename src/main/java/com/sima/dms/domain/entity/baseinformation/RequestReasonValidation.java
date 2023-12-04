package com.sima.dms.domain.entity.baseinformation;

import com.sima.dms.domain.enums.FieldNameEnum;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DMS_REQUEST_REASON_VALIDATION")
public class RequestReasonValidation {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIELD_NAME", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private FieldNameEnum fieldName;

    @ManyToOne
    @JoinColumn(name = "REQUEST_REASON_Id", nullable = false)
    private DocumentRequestReason requestReason;

    @Column(name = "REQUIRED")
    private Boolean required = false;
}
