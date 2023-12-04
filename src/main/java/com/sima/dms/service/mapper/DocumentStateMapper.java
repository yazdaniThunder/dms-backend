package com.sima.dms.service.mapper;


import com.sima.dms.domain.dto.document.DocumentStateDto;
import com.sima.dms.domain.entity.document.DocumentState;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface DocumentStateMapper extends EntityMapper<DocumentStateDto, DocumentState> {

    @Mapping(source = "createdBy.id", target = "userId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    DocumentStateDto toDto(DocumentState state);

    @Mapping(source = "userId", target = "createdBy")
//    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentState toEntity(DocumentStateDto dto);

    @AfterMapping
    default void setUsername(@MappingTarget DocumentStateDto dto, DocumentState entity) {
        if (entity.getCreatedBy()!=null && entity.getCreatedBy().getUser() != null)
            dto.setUsername(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());

        dto.setSeen(entity.getProfileSeen() != null && !entity.getProfileSeen().isEmpty() && entity.getProfileSeen().stream().anyMatch(profile -> profile.getId().equals(currentUser().getId())));
    }

    default DocumentState fromId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentState state = new DocumentState();
        state.setId(id);
        return state;
    }
}
