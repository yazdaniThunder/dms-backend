package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.MetadataTreeDto;
import com.sima.dms.domain.entity.MetadataTree;
import com.sima.dms.repository.MetadataTreeRepository;
import com.sima.dms.service.MetadataTreeService;
import com.sima.dms.service.mapper.MetadataTreeMapper;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.sima.dms.constants.OpenKM;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sima.dms.utils.Responses.notFound;

@Service
//@Transactional
@AllArgsConstructor
public class MetadataTreeServiceImpl implements MetadataTreeService {

    private final MetadataTreeMapper metadataTreeMapper;
    private final MetadataTreeRepository metadataTreeRepository;
    private final Logger log = LoggerFactory.getLogger(MetadataTreeServiceImpl.class);
    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public List<MetadataTreeDto> save(List<MetadataTreeDto> metadataTreeDtos) {
        log.debug("Request to save Metadata Tree : {}", metadataTreeDtos);
        List<MetadataTree> dto = metadataTreeRepository.saveAll(metadataTreeMapper.toEntity(metadataTreeDtos));
        return metadataTreeMapper.toDto(dto);
    }

    @Override
    public List<MetadataTreeDto> save(Long parentId, List<String> names) {
        log.debug("Request to save Metadata Tree : {}", parentId, names);
        List<MetadataTree> metadataTree = new ArrayList<>();
        names.forEach(name -> {
            metadataTree.add(new MetadataTree(name, metadataTreeMapper.formId(parentId)));
        });
        List<MetadataTree> result = metadataTreeRepository.saveAll(metadataTree);
        return metadataTreeMapper.toDto(result);
    }


    @Override
    public MetadataTreeDto findById(Long id) {
        log.debug("Request to get Metadata Tree : {}", id);
        MetadataTree metadataTree = metadataTreeRepository.findById(id)
                .orElseThrow(() -> Responses.notFound("Metadata Tree not found"));
        return metadataTreeMapper.toDto(metadataTree);
    }

    @Override
    public List<MetadataTreeDto> findByPatent(Long parentId) {
        log.debug("Request to get Metadata Trees by parent : {}", parentId);
        return metadataTreeMapper.toDto(metadataTreeRepository.findAllByParentId(parentId));
    }

    @Override
//    @Transactional(readOnly = true)
    public List<MetadataTreeDto> getMetadataTree() {
        log.debug("Request to get all Metadata Tree : {}");
        return metadataTreeMapper.toDto(metadataTreeRepository.findAllByParentId(null));
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Metadata Tree : {}", id);
        metadataTreeRepository.deleteById(id);
    }



}
