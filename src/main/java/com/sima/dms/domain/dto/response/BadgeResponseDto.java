package com.sima.dms.domain.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "BadgeResponseObject")
public class BadgeResponseDto {
    private Long documentSetByBranchCount;
    private Long acceptWaitingCount;
    private Long fixedConflictedDocumentsCount;
    private Long conflictingCount;
    private Long sentConflictedDocumentCount;
    private Long documentRequestCount;
    private Long otherDocumentCount;
    private Long totalCount;

    public BadgeResponseDto(Long documentSetByBranchCount, Long acceptWaitingCount, Long fixedConflictedDocumentsCount,
                            Long conflictingCount, Long sentConflictedDocumentCount, Long documentRequestCount, Long otherDocumentCount) {
        this.documentSetByBranchCount = documentSetByBranchCount;
        this.acceptWaitingCount = acceptWaitingCount;
        this.fixedConflictedDocumentsCount  = fixedConflictedDocumentsCount;
        this.conflictingCount = conflictingCount ;
        this.sentConflictedDocumentCount = sentConflictedDocumentCount ;
        this.documentRequestCount = documentRequestCount ;
        this.otherDocumentCount = otherDocumentCount;

    }

    public long getTotalCount() {
        return this.documentSetByBranchCount + this.acceptWaitingCount +
                this.fixedConflictedDocumentsCount + this.conflictingCount +
                this.sentConflictedDocumentCount + this.documentRequestCount + this.otherDocumentCount;

    }
}
