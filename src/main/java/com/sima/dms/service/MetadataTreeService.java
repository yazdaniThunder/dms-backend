package com.sima.dms.service;

import com.sima.dms.domain.dto.MetadataTreeDto;

import java.util.List;

public interface MetadataTreeService {

    List<MetadataTreeDto> save(List<MetadataTreeDto> metadataTreeDtos);

    List<MetadataTreeDto> save(Long parentId, List<String> names);

    MetadataTreeDto findById(Long id);

    List<MetadataTreeDto> findByPatent(Long parentId);

    List<MetadataTreeDto> getMetadataTree();

    void delete(Long id);


}
