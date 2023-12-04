package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.entity.NodeDocument;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = {NodeBaseMapper.class})
public interface NodeDocumentMapper extends EntityMapper<NodeDocumentDto, NodeDocument> {

    NodeDocumentDto toDto(NodeDocument nodeDocument);

    NodeDocument toEntity(NodeDocumentDto document);

//    @AfterMapping
//    default void setThumbnailLink(@MappingTarget NodeDocumentDto nodeDocumentDto, NodeDocument nodeDocument) {
//
//        if (nodeDocument.getThumbnails() != null && !nodeDocument.getThumbnails().isEmpty())
//            nodeDocumentDto.setThumbnailLink("http://okmAdmin:admin@localhost:4200/OpenKM/Download?uuid=" + nodeDocument.getThumbnails().stream().findFirst().get());
//
//    }

    default NodeDocument fromId(String uuid) {
        if (uuid == null) {
            return null;
        }
        NodeDocument nodeDocument = new NodeDocument();
        nodeDocument.setUuid(uuid);
        return nodeDocument;
    }
}