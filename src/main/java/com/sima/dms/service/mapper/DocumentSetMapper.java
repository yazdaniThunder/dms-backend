package com.sima.dms.service.mapper;


import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetDto;
import com.sima.dms.domain.entity.documentSet.DocumentSet;
import com.sima.dms.domain.entity.documentSet.DocumentSetState;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.enums.DocumentSetStateEnum.*;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, DocumentMapper.class, DocumentSetStateMapper.class,
        DocumentSetConflictMapper.class,FileStatusMapper.class})
public interface DocumentSetMapper extends EntityMapper<DocumentSetDto, DocumentSet> {

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    @Mapping(source = "fileStatusId", target = "fileStatus")
    DocumentSet toEntity(DocumentSetDto documentSetDto);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    @Mapping(source = "fileStatus.fileType.title", target = "fileTypeTitle")
    @Mapping(source = "fileStatus.id", target = "fileStatusId")
    @Mapping(source = "fileStatus.title", target = "fileStatusTitle")
    DocumentSetDto toDto(DocumentSet documentSet);

    @AfterMapping
    default void setProperties(@MappingTarget DocumentSetDto documentSetDto, DocumentSet documentSet) {

        if (documentSet.getState() != null)
            documentSetDto.setCurrentState(documentSet.getState().getName());

        if (documentSet.getFirstState().getCreatedBy().getUser() != null) {
            documentSetDto.setRegistrarName(documentSet.getFirstState().getCreatedBy().getUser().getFirstName() + " " + documentSet.getFirstState().getCreatedBy().getUser().getLastName());

        }
        if (documentSet.getBranch() != null)
            documentSetDto.setBranch(new BranchDto(documentSet.getBranch()));

        if (documentSet.getStates().stream().anyMatch(state -> state.getName().equals(BRANCH_CONFIRMED))) {
            DocumentSetState documentSetState = documentSet.getStates().stream().filter(state -> state.getName().equals(BRANCH_CONFIRMED)).findFirst().get();
            documentSetDto.setSendDate(documentSetState.getRegisterDate());
            if (documentSetState.getCreatedBy().getUser() != null)
                documentSetDto.setConfirmerName(documentSetState.getCreatedBy().getUser().getFirstName() + " " + documentSetState.getCreatedBy().getUser().getLastName());
        }
        if (documentSet.getStates().stream().anyMatch(state -> state.getName().equals(SCANNED))) {
            DocumentSetState documentSetState = documentSet.getStates().stream().filter(state -> state.getName().equals(SCANNED)).reduce((first, second) -> second).get();
            if (documentSetState.getCreatedBy().getUser() != null && !documentSet.getState().getName().equals(PRIMARY_CONFIRMED))
                documentSetDto.setScannerName(documentSetState.getCreatedBy().getUser().getFirstName() + " " + documentSetState.getCreatedBy().getUser().getLastName());
        }

        if (documentSet.getStates().stream().anyMatch(state -> state.getName().equals(PRIMARY_CONFIRMED))) {
            DocumentSetState documentSetState = documentSet.getStates().stream().filter(state -> state.getName().equals(PRIMARY_CONFIRMED)).reduce((first, second) -> second).get();
            documentSetDto.setPrimaryConfirmedDate(documentSetState.getRegisterDate());
        }
        if (documentSet.getStates().stream().anyMatch(state -> state.getName().equals(CONFLICTING))) {
            DocumentSetState documentSetState = documentSet.getStates().stream().filter(state -> state.getName().equals(CONFLICTING)).reduce((first, second) -> second).get();
            documentSetDto.setConflictingDate(documentSetState.getRegisterDate());
        }

        if (documentSet.getStates().stream().anyMatch(state -> state.getName().equals(FIX_CONFLICT))) {
            DocumentSetState documentSetState = documentSet.getStates().stream().filter(state -> state.getName().equals(FIX_CONFLICT)).reduce((first, second) -> second).get();
            documentSetDto.setFixConflictDate(documentSetState.getRegisterDate());
        }

        if (documentSet.getStates().stream().anyMatch(state -> state.getName().equals(PRIMARY_CONFIRMED))) {
            DocumentSetState documentSetState = documentSet.getStates().stream().filter(state -> state.getName().equals(PRIMARY_CONFIRMED)).reduce((first, second) -> second).get();
            documentSetDto.setFixConflictDate(documentSetState.getRegisterDate());
        }
    }

    default DocumentSet fromId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentSet documentSet = new DocumentSet();
        documentSet.setId(id);
        return documentSet;
    }

}
