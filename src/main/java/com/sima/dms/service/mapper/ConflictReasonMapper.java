package com.sima.dms.service.mapper;


import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface ConflictReasonMapper extends EntityMapper<ConflictReasonDto, ConflictReason> {

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    ConflictReason toEntity(ConflictReasonDto conflictReasonDto);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    ConflictReasonDto toDto(ConflictReason conflictReason);

    @AfterMapping
    default void set(@MappingTarget ConflictReasonDto dto, ConflictReason entity) {
        if (entity.getCreatedBy() !=null && entity.getCreatedBy().getUser() != null)
            dto.setCreateByFullName(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());
    }

    default ConflictReason formId(Long id) {
        if (id == null) {
            return null;
        }
        ConflictReason reason = new ConflictReason();
        reason.setId(id);
        return reason;
    }

    default ConflictReasonDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        ConflictReasonDto reason = new ConflictReasonDto();
        reason.setId(id);
        return reason;
    }

}
