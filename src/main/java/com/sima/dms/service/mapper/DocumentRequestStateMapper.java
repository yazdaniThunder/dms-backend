package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.DocumentRequestStateDto;
import com.sima.dms.domain.entity.document.DocumentRequestState;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;

@Mapper(componentModel = "spring",uses ={ProfileMapper.class} )
public interface DocumentRequestStateMapper {

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    DocumentRequestStateDto toDto(DocumentRequestState state);

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentRequestState toEntity(DocumentRequestStateDto dto);

    @AfterMapping
    default void setProperties(@MappingTarget DocumentRequestStateDto documentRequestStateDto , DocumentRequestState documentRequestState ){
        if (documentRequestState.getCreatedBy().getUser() != null)
            documentRequestStateDto.setCreateByFullName(documentRequestState.getCreatedBy().getUser().getFirstName() + " " + documentRequestState.getCreatedBy().getUser().getLastName());

        documentRequestStateDto.setSeen(documentRequestState.getProfileSeen() != null && !documentRequestState.getProfileSeen().isEmpty() && documentRequestState.getProfileSeen().stream().anyMatch(profile -> profile.getId().equals(currentUser().getId())));

    }
}
