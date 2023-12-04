package com.sima.dms.service;

import com.sima.dms.domain.dto.baseinformation.FileTypeDto;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FileTypeService {
    FileTypeDto save(FileTypeDto fileTypeDto);

    FileTypeDto update(FileTypeDto fileTypeDto);

    FileTypeDto getById(Long id);

    void deleteById(Long id);

    void deleteByIds(List<Long> ids);

    Page<FileTypeDto> getAll(Pageable pageable);

    List<FileTypeDto> getList();

    void updateActive(List<Long> ids, boolean active);

    FileTypeDto getByTitle(DocumentSetTypeEnum title);
}
