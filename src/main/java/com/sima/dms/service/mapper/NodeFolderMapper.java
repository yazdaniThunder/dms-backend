package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.NodeFolderDto;
import com.sima.dms.domain.entity.NodeFolder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface NodeFolderMapper extends EntityMapper<NodeFolderDto, NodeFolder> {

    NodeFolderDto toDto(NodeFolder nodeFolder);

    NodeFolder toEntity(NodeFolderDto nodeFolderDto);

    default NodeFolder fromId(String uuid) {
        if (uuid == null) {
            return null;
        }
        NodeFolder nodeFolder = new NodeFolder();
        nodeFolder.setUuid(uuid);
        return nodeFolder;
    }
}
