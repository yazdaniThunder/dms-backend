package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.baseinformation.FileTypeDto;
import com.sima.dms.domain.dto.document.OtherDocumentFileDto;
import com.sima.dms.domain.entity.baseinformation.FileType;
import com.sima.dms.domain.entity.baseinformation.OtherDocumentType;
import com.sima.dms.domain.entity.document.OtherDocument;
import com.sima.dms.domain.entity.document.OtherDocumentFile;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.OtherDocumentStateEnum;
import com.sima.dms.repository.FileTypeRepository;
import com.sima.dms.repository.OtherDocumentFileRepository;
import com.sima.dms.repository.OtherDocumentRepository;
import com.sima.dms.repository.OtherDocumentTypeRepository;
import com.sima.dms.service.FileTypeService;
import com.sima.dms.service.mapper.FileStatusMapper;
import com.sima.dms.service.mapper.FileTypeMapper;
import com.sima.dms.service.mapper.OtherDocumentTypeMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sima.dms.utils.Responses.notFound;

@Service
@AllArgsConstructor
public class FileTypeServiceImpl implements FileTypeService {

    private final FileTypeRepository fileTypeRepository;
    private final OtherDocumentRepository otherDocumentRepository;
    private final OtherDocumentFileRepository otherDocumentFileRepository;
    private final OtherDocumentTypeRepository otherDocumentTypeRepository;

    private final FileTypeMapper fileTypeMapper;
    private final FileStatusMapper fileStatusMapper;
    private final OtherDocumentTypeMapper otherDocumentTypeMapper;

    private final Logger log = LoggerFactory.getLogger(FileTypeServiceImpl.class);

    @Override
    public FileTypeDto save(FileTypeDto fileTypeDto) {
        log.debug("Request to save file type {}", fileTypeDto);
        FileType fileType = fileTypeMapper.toEntity(fileTypeDto);
//        if (fileType.getFileStatuses() != null && fileType.getFileStatuses().get(0) != null)
//            fileType.getFileStatuses().get(0).setIsDefault(true);
        return fileTypeMapper.toDto(fileTypeRepository.save(fileType));
    }

    @Override
    public FileTypeDto update(FileTypeDto fileTypeDto) {
        log.debug("Request to update file type {}", fileTypeDto);
        FileType fileType = fileTypeRepository.findById(fileTypeDto.getId()).orElseThrow(() -> notFound("file type not fund"));
        if (fileTypeDto.getTitle() != null) {
            fileType.setTitle(fileTypeDto.getTitle());
        }
        if (fileTypeDto.getActivateFileNumber() != null) {
            fileType.setActivateFileNumber(fileTypeDto.getActivateFileNumber());
        }
        if (fileType.getFileStatuses() != null) {
            fileType.getFileStatuses().forEach(fileStatus -> {
                if (fileStatus.getIsDefault()) {
                    fileStatus.setIsDefault(false);
                }
            });
        }
        List<OtherDocument> otherDocuments = otherDocumentRepository.getAllByFileTypeId(fileType.getId(), Arrays.asList(OtherDocumentStateEnum.BRANCH_REJECTED, OtherDocumentStateEnum.REGISTERED));

        if (fileTypeDto.getOtherDocumentTypes() != null) {
            List<OtherDocumentType> otherDocumentTypes = new ArrayList<>();
            fileTypeDto.getOtherDocumentTypes().forEach(otherDocumentTypeDto -> {
                if (otherDocumentTypeDto.getId() == null) {
                    OtherDocumentType otherDocumentType = otherDocumentTypeMapper.toEntity(otherDocumentTypeDto);
                    otherDocumentType.setFileType(fileType);
                    otherDocumentTypeRepository.save(otherDocumentType);
                    otherDocumentTypes.add(otherDocumentType);
                }

            });
            if (!otherDocumentTypes.isEmpty()) {
                otherDocuments.forEach(otherDocument -> {
                    otherDocumentTypes.forEach(otherDocumentType -> {
                        OtherDocumentFile otherDocumentFile = new OtherDocumentFile(otherDocument, otherDocumentType);
                        otherDocumentFileRepository.save(otherDocumentFile);

                    });
                });
            }
            fileTypeDto.getOtherDocumentTypes().forEach(otherDocumentTypeDto -> {
                if (otherDocumentTypeDto.getId() != null)
                    otherDocumentTypes.add(otherDocumentTypeMapper.toEntity(otherDocumentTypeDto));
            });
            fileType.setOtherDocumentTypes(otherDocumentTypes);
        }
        fileType.setFileStatuses(fileStatusMapper.toEntity(fileTypeDto.getFileStatuses()));
        return fileTypeMapper.toDto(fileTypeRepository.save(fileType));
    }

    @Override
    public FileTypeDto getById(Long id) {
        log.debug("Request to get by id {}", id);
        FileType fileType = fileTypeRepository.findById(id).orElseThrow(() -> notFound("file type not fund"));
        return fileTypeMapper.toDto(fileType);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Request to delete file Type by id : {}", id);
        fileTypeRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        log.debug("Request to delete All file Type By ids : {}", ids);
        fileTypeRepository.deleteAllById(ids);

    }

    @Override
    public Page<FileTypeDto> getAll(Pageable pageable) {
        log.debug("Request to get all file type ");
        return fileTypeRepository.findAll(pageable).map(fileTypeMapper::toDto);
    }

    @Override
    public List<FileTypeDto> getList() {
        log.debug("Request to get all file type by active is true");
        return fileTypeRepository.getAllByActiveIsTrue().stream().map(fileTypeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateActive(List<Long> ids, boolean active) {
        log.debug("Request to update file type : {}", ids, active);
        fileTypeRepository.updateActive(ids, active);
    }

    @Override
    public FileTypeDto getByTitle(DocumentSetTypeEnum title) {
        log.debug("Request to get file type by title {}: ", title);
        return fileTypeMapper.toDto(fileTypeRepository.getByTitle(title.getTitle()));
    }
}
