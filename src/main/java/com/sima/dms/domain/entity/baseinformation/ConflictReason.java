package com.sima.dms.domain.entity.baseinformation;

import com.sima.dms.domain.entity.common.Auditable;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DMS_CONFLICT_REASON")
public class ConflictReason extends Auditable {

    @Column(name = "DOCUMENT_SET_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentSetTypeEnum documentSetType;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ConflictTypeEnum type;

    @Column(name = "REASON")
    private String reason;
}
