package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.MetadataTreeDto;
import com.sima.dms.domain.entity.MetadataTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MetadataTreeMapper extends EntityMapper<MetadataTreeDto, MetadataTree> {

    MetadataTree toEntity(MetadataTreeDto metadataTreeDto);

    @Mapping(source = "parent.id", target = "parentId")
    MetadataTreeDto toDto(MetadataTree metadataTree);

    default MetadataTree formId(Long id) {
        if (id == null) {
            return null;
        }
        MetadataTree reasons = new MetadataTree();
        reasons.setId(id);
        return reasons;
    }
}
