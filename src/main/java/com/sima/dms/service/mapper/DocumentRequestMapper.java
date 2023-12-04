package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.DocumentRequestDto;
import com.sima.dms.domain.entity.document.DocumentRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, BranchMapper.class,DocumentRequestStateMapper.class, NodeDocumentMapper.class,DocumentRequestReasonMapper.class})
public interface DocumentRequestMapper extends EntityMapper<DocumentRequestDto, DocumentRequest> {

    @Mapping(source = "branchId", target = "branch")
    @Mapping(source = "documentRequestReasonId", target = "documentRequestReason")
    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentRequest toEntity(DocumentRequestDto dto);

    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.branchCode", target = "documentBranchCode")
    @Mapping(source = "branch.branchName", target = "documentBranchName")
    @Mapping(source = "requestBranch.branchCode", target = "requestBranchCode")
    @Mapping(source = "requestBranch.branchName", target = "requestBranchName")
    @Mapping(source = "branchFile.uuid", target = "branchFileUuid")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    @Mapping(source = "documentRequestReason.id", target = "documentRequestReasonId")
    @Mapping(source = "documentRequestReason.title", target = "documentRequestReasonTitle")
    DocumentRequestDto toDto(DocumentRequest request);

    @AfterMapping
    default void setProperties(@MappingTarget DocumentRequestDto dto, DocumentRequest entity) {

        if (entity.getCreatedBy().getUser() != null)
            dto.setRegistrarName(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());

    }

    default DocumentRequest formId(Long id) {

        if (id == null) {
            return null;
        }
        DocumentRequest request = new DocumentRequest();
        request.setId(id);
        return request;
    }
}
