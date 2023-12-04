package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.NodeBaseDto;
import com.sima.dms.domain.entity.NodeBase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface NodeBaseMapper extends EntityMapper<NodeBaseDto, NodeBase> {

    NodeBaseDto toDto(NodeBase nodeBase);

    NodeBase toEntity(NodeBaseDto nodeBaseDto);

    default NodeBase fromId(String uuid) {
        if (uuid == null) {
            return null;
        }
        NodeBase nodeBase = new NodeBase();
        nodeBase.setUuid(uuid);
        return nodeBase;
    }
}
