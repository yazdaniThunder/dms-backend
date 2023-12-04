package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.DocumentOcrDto;
import com.sima.dms.domain.entity.DocumentOcr;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", uses = NodeDocumentMapper.class)
public interface DocumentOcrMapper extends EntityMapper<DocumentOcrDto, DocumentOcr> {

    @Mapping(source = "nodeDocument.uuid", target = "nodeDocumentUuid")
    DocumentOcrDto toDto(DocumentOcr documentOcr);

    @Mapping(source = "nodeDocumentUuid", target = "nodeDocument.uuid")
    DocumentOcr toEntity(DocumentOcrDto documentOcrDto);

    default DocumentOcr fromId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentOcr documentOcr = new DocumentOcr();
        documentOcr.setId(id);
        return documentOcr;
    }

    default Set<String> mapEveryThing(String map) {
        if (map == null) {
            return Collections.emptySet();
        }

        return new HashSet<>(Arrays.asList(map.split(",")));
    }

    default String mapEveryThing(Set<String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return String.join(",", map);
    }

}

