package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.OtherDocumentStateDto;
import com.sima.dms.domain.entity.document.OtherDocumentState;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;

@Mapper(componentModel = "spring",uses ={ProfileMapper.class} )
public interface OtherDocumentStateMapper extends EntityMapper<OtherDocumentStateDto, OtherDocumentState>{

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    OtherDocumentStateDto toDto(OtherDocumentState state);

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    OtherDocumentState toEntity(OtherDocumentStateDto dto);

    @AfterMapping
    default void setProperties(@MappingTarget OtherDocumentStateDto otherDocumentStateDto , OtherDocumentState otherDocumentState ){
        if (otherDocumentState.getCreatedBy().getUser() != null)
            otherDocumentStateDto.setCreateByFullName(otherDocumentState.getCreatedBy().getUser().getFirstName() + " " + otherDocumentState.getCreatedBy().getUser().getLastName());

        otherDocumentStateDto.setSeen(otherDocumentState.getProfileSeen() != null && !otherDocumentState.getProfileSeen().isEmpty() && otherDocumentState.getProfileSeen().stream().anyMatch(profile -> profile.getId().equals(currentUser().getId())));

    }
}
