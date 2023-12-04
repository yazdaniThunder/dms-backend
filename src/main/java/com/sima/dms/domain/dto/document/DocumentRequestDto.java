package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.DocumentRequestTypeEnum;
import com.sima.dms.domain.enums.RequestTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.List;


@Data
@NoArgsConstructor
@ApiModel(value = "DocumentRequestObject")
public class DocumentRequestDto extends BaseDto {

    @NonNull
    private RequestTypeEnum requestType;
    @NonNull
    private DocumentRequestTypeEnum documentType;

    @NonNull
    private Long documentRequestReasonId;

    @Schema(hidden = true)
    private String documentRequestReasonTitle;

    private String fileNumber;

    private String fileTitle;

    private Instant fileDate;

    private Instant fileDateFrom;

    private Instant fileDateTo;

    private String documentNumber;

    private Instant checkDate;

    private String checkNumber;

    private Instant documentDate;

    private Instant documentDateFrom;

    private Instant documentDateTo;

    private Long documentAmount;

    private String customerNumber;

    private Long branchId;

    private String requestDescription;

    private String referenceTitle;

    @Schema(hidden = true)
    private String branchFileUuid;

    @Schema(hidden = true)
    private String receiveDescription;

    @Schema(hidden = true)
    private Instant expiryDate;

    @Schema(hidden = true)
    private String documentBranchCode;

    @Schema(hidden = true)
    private String documentBranchName;

    @Schema(hidden = true)
    private String registrarName;

    @Schema(hidden = true)
    private String requestBranchName;

    @Schema(hidden = true)
    private Long requestBranchCode;

    private Instant checkReceiptDate;

    private Instant checkReceiptDateFrom;

    private Instant checkReceiptDateTo;

    private String checkIssuingBank;

    @Schema(hidden = true)
    private DocumentRequestStateDto lastState;

    @Schema(hidden = true)
    private List<DocumentRequestStateDto> states;

    @Schema(hidden = true)
    private List<NodeDocumentDto> officeFiles;
}
