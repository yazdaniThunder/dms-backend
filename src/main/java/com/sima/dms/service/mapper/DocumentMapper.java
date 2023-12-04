package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.entity.document.Document;
import com.sima.dms.domain.entity.document.DocumentState;
import com.sima.dms.domain.entity.documentSet.DocumentSetState;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import com.sima.dms.domain.enums.DocumentStateEnum;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED;
import static com.sima.dms.domain.enums.DocumentSetStateEnum.SCANNED;
import static com.sima.dms.domain.enums.DocumentStateEnum.FIX_CONFLICT;

@Mapper(componentModel = "spring", uses = {DocumentSetMapper.class, NodeDocumentMapper.class, ProfileMapper.class, DocumentStateMapper.class, DocumentConflictMapper.class})
public interface DocumentMapper extends EntityMapper<DocumentDto, Document> {

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    @Mapping(source = "primaryApproverId", target = "primaryApprover")
    Document toEntity(DocumentDto documentDto);

    @Mapping(source = "file.uuid", target = "fileUuid")
    @Mapping(source = "documentSet.fromDate", target = "fromDate")
    @Mapping(source = "documentSet.toDate", target = "toDate")
    @Mapping(source = "documentSet.id", target = "documentSetId")
    @Mapping(source = "documentSet.type", target = "type")
    @Mapping(source = "primaryApprover.id", target = "primaryApproverId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    DocumentDto toDto(Document document);

    @AfterMapping
    default void setProperties(@MappingTarget DocumentDto documentDto, Document document) {

        if (document.getState() != null)
            documentDto.setCurrentState(document.getState().getName());

        if (document.getPrimaryApprover() != null && document.getPrimaryApprover().getUser() != null)
            documentDto.setPrimaryApproverName(document.getPrimaryApprover().getUser().getFirstName() + " " + document.getPrimaryApprover().getUser().getLastName());

        if (document.getDocumentSet().getRowsNumber() != null && document.getDocumentSet().getSequence() != null)
            documentDto.setDocumentSetRowsNumber(document.getDocumentSet().getRowsNumber() + document.getDocumentSet().getSequence());

        if (document.getDocumentSet().getFirstState().getCreatedBy().getBranch() != null) {
            documentDto.setBranchCode(document.getDocumentSet().getFirstState().getCreatedBy().getBranch().getBranchCode());
            documentDto.setBranchName(document.getDocumentSet().getFirstState().getCreatedBy().getBranch().getBranchName());
        }

        if (document.getDocumentSet().getStates().stream().anyMatch(state -> state.getName().equals(BRANCH_CONFIRMED)))
            documentDto.setSendDate(document.getDocumentSet().getStates().stream().filter(state -> state.getName().equals(BRANCH_CONFIRMED)).findFirst().get().getRegisterDate());

        if (document.getDocumentSet().getStates().stream().anyMatch(state -> state.getName().equals(SCANNED))) {
            DocumentSetState documentSetState = document.getDocumentSet().getStates().stream().filter(state -> state.getName().equals(SCANNED)).findFirst().get();
            documentDto.setRegisterDate(documentSetState.getRegisterDate());
            documentDto.setScannerName(documentSetState.getCreatedBy().getUser().getFirstName() + " " + documentSetState.getCreatedBy().getUser().getLastName());
        }

        if (document.getStates().stream().anyMatch(state -> state.getName().equals(FIX_CONFLICT))) {
            DocumentState documentState = document.getStates().stream().filter(state -> state.getName().equals(DocumentStateEnum.FIX_CONFLICT)).reduce((first, second) -> second).get();
            documentDto.setPrimaryConfirmedDate(documentState.getRegisterDate());
        }

        if (document.getStates().stream().anyMatch(state -> state.getName().equals(DocumentStateEnum.SENT_CONFLICT))) {
            DocumentState documentState = document.getStates().stream().filter(state -> state.getName().equals(DocumentStateEnum.SENT_CONFLICT)).reduce((first, second) -> second).get();
            documentDto.setSentConflictDate(documentState.getRegisterDate());
        }
        if (document.getDocumentSet().getOcrFinishedTime() != null)
            documentDto.setOcrFinishedTime(document.getDocumentSet().getOcrFinishedTime());
    }

    default Document fromId(Long id) {
        if (id == null) {
            return null;
        }
        Document document = new Document();
        document.setId(id);
        return document;
    }

}
