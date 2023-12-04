package com.sima.dms.domain.entity.baseinformation;

import com.sima.dms.domain.entity.common.Auditable;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "DMS_DOCUMENT_REQUEST_REASON")
public class DocumentRequestReason extends Auditable {

    @Column(name = "title",nullable = false)
    private String title;

    @OneToMany(mappedBy = "requestReason", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RequestReasonValidation> requestReasonValidations = new ArrayList<>();

    public void setRequestReasonValidations(List<RequestReasonValidation> requestReasonValidations) {
        this.requestReasonValidations.clear();
        if (requestReasonValidations != null) {
            requestReasonValidations.forEach(requestReasonValidation ->  requestReasonValidation.setRequestReason(this));
            this.requestReasonValidations.addAll(requestReasonValidations);
        }
    }

}
