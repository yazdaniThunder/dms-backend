package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.documentSet.DocumentSetStateDto;
import com.sima.dms.domain.entity.documentSet.DocumentSetState;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, UserMapper.class})
public interface DocumentSetStateMapper extends EntityMapper<DocumentSetStateDto, DocumentSetState> {

    //    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    @Mapping(source = "createdBy.user.firstName", target = "username")
    DocumentSetStateDto toDto(DocumentSetState documentSetState);

    //@Mapping(source = "userId", target = "createdBy")
    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentSetState toEntity(DocumentSetStateDto documentSetStateDto);

    @AfterMapping
    default void setUsername(@MappingTarget DocumentSetStateDto dto, DocumentSetState entity) {
        if (entity.getCreatedBy() != null && entity.getCreatedBy().getUser() != null)
            dto.setUsername(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());

        dto.setSeen(entity.getProfileSeen() != null && !entity.getProfileSeen().isEmpty() && entity.getProfileSeen().stream().anyMatch(profile -> profile.getId().equals(currentUser().getId())));
    }

    default DocumentSetState fromId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentSetState documentSetState = new DocumentSetState();
        documentSetState.setId(id);
        return documentSetState;
    }
}
