package com.sima.dms.service.impl;

import com.sima.dms.constants.OpenKM;
import com.sima.dms.domain.dto.document.OtherDocumentDto;
import com.sima.dms.domain.dto.document.OtherDocumentFileDto;
import com.sima.dms.domain.dto.document.OtherDocumentStateDto;
import com.sima.dms.domain.dto.request.AdvanceOtherDocumentSearchDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.NodeDocument;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.baseinformation.FileType;
import com.sima.dms.domain.entity.baseinformation.OtherDocumentType;
import com.sima.dms.domain.entity.document.OtherDocument;
import com.sima.dms.domain.entity.document.OtherDocumentFile;
import com.sima.dms.domain.entity.document.OtherDocumentState;
import com.sima.dms.domain.enums.ObjectName;
import com.sima.dms.domain.enums.OtherDocumentStateEnum;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.domain.enums.WorkflowOperationState;
import com.sima.dms.repository.*;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.NodeDocumentService;
import com.sima.dms.service.OtherDocumentService;
import com.sima.dms.service.PermissionService;
import com.sima.dms.service.mapper.*;
import com.sima.dms.tools.FileUtils;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.enums.RoleEnum.BA;
import static com.sima.dms.domain.enums.RoleEnum.BU;
import static com.sima.dms.utils.DateUtils.toDate;
import static com.sima.dms.utils.Responses.*;

@Service
@AllArgsConstructor
public class OtherDocumentServiceImpl implements OtherDocumentService {

    private final FolderService folderService;
    private final PermissionService permissionService;
    private final NodeDocumentService nodeDocumentService;
    private final OtherDocumentRepository otherDocumentRepository;
    private final ProfileRepository profileRepository;
    private final OtherDocumentStateRepository otherDocumentStateRepository;
    private final BranchRepository branchRepository;
    private final FileTypeRepository fileTypeRepository;
    private final OtherDocumentFileRepository otherDocumentFileRepository;
    private final NodeBaseRepository nodeBaseRepository;

    private final OtherDocumentMapper otherDocumentMapper;
    private final OtherDocumentStateMapper otherDocumentStateMapper;
    private final FileStatusMapper fileStatusMapper;
    private final OtherDocumentFileMapper otherDocumentFileMapper;
    private final Logger log = LoggerFactory.getLogger(OtherDocumentServiceImpl.class);

    @Override
    public OtherDocumentDto save(OtherDocumentDto otherDocumentDto) {
        log.debug("Request to save other Document : {}", otherDocumentDto);
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        if (otherDocumentDto.getCustomerNumber() == null || otherDocumentDto.getCustomerNumber().isEmpty())
            throw badRequest("customerNumber must not be null");
        if (otherDocumentDto.getFileTypeId() == null)
            throw badRequest("fileFile must not be null");

        FileType fileType = fileTypeRepository.findById(otherDocumentDto.getFileTypeId()).orElseThrow(() -> notFound("fileType not found"));
        if (fileType.getActivateFileNumber().equals(false) && otherDocumentDto.getFileNumber() != null && !otherDocumentDto.getFileNumber().isEmpty())
            throw badRequest("fileNumber must  be null");
        else if (fileType.getActivateFileNumber().equals(true) && (otherDocumentDto.getFileNumber() == null || otherDocumentDto.getFileNumber().isEmpty()))
            throw badRequest("fileNumber must not be null");

        OtherDocument otherDocument = otherDocumentMapper.toEntity(otherDocumentDto);
        List<OtherDocumentFile> otherDocumentFiles = new ArrayList<>();
        fileType.getOtherDocumentTypes().forEach(otherDocumentType -> {
            OtherDocumentFile otherDocumentFile = new OtherDocumentFile(otherDocument, otherDocumentType);
            otherDocumentFiles.add(otherDocumentFile);
        });
        otherDocument.setOtherDocumentFiles(otherDocumentFiles);
        otherDocument.setBranch(profile.getBranch());
        otherDocument.setLastState(new OtherDocumentState(OtherDocumentStateEnum.REGISTERED, otherDocument));
        return otherDocumentMapper.toDto(otherDocumentRepository.save(otherDocument));
    }

    @Override
    public OtherDocumentDto updateOtherDocumentFile(OtherDocumentFileDto otherDocumentFileDto, MultipartFile file) throws IOException {
        log.debug("Request to update  update Other Document File : {}", otherDocumentFileDto);
        com.openkm.sdk4j.bean.Document document = new com.openkm.sdk4j.bean.Document();
        OtherDocumentFile otherDocumentFile = otherDocumentFileRepository.findById(otherDocumentFileDto.getId()).orElseThrow(() -> notFound("otherDocumentFile not found"));
        if (!otherDocumentFile.getOtherDocument().getLastState().getState().equals(OtherDocumentStateEnum.REGISTERED) &&
                !otherDocumentFile.getOtherDocument().getLastState().getState().equals(OtherDocumentStateEnum.BRANCH_REJECTED))
            throw forbidden("Condition not valid");
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 10000000)
                throw Responses.badRequest("The file size is more than 10 MB");
            String fileFormat = FileNameUtils.getExtension(file.getOriginalFilename());
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            if (!mimeType.equals("image/tif") && !mimeType.equals("image/tiff") && !mimeType.equals("application/pdf")) {
                throw Responses.badRequest("The file mimeType must be tif or pdf");
            }
            if (!fileFormat.equals("tif") && !fileFormat.equals("tiff") && !fileFormat.equals("pdf")) {
                throw Responses.badRequest("The file format must be tif or pdf");
            }
            NodeDocument nodeDocument = otherDocumentFile.getFile();
            otherDocumentFile.setFileStatus(fileStatusMapper.formId(otherDocumentFileDto.getFileStatusId()));
            otherDocumentFileDto = otherDocumentFileMapper.toDto(otherDocumentFileRepository.save(otherDocumentFile));
            if (nodeDocument != null && mimeType.equals(nodeDocument.getMimeType()))
                nodeDocumentService.updateDocument(nodeDocument.getUuid(), file.getInputStream());
            else if (nodeDocument != null && !mimeType.equals(nodeDocument.getMimeType())) {
                String path = this.getTempPath();
                document.setPath(path + "/" + FileUtils.uniqueName(otherDocumentFile.getOtherDocument().getCustomerNumber()) + '.' + fileFormat);
                document = nodeDocumentService.createDocument(document, file.getInputStream());
                otherDocumentFileRepository.updateUuid(nodeDocument.getUuid(), document.getUuid());
                nodeDocumentService.deleteDocument(nodeDocument.getUuid());
                nodeBaseRepository.deleteByUuid(nodeDocument.getUuid());
                otherDocumentFileDto.setFileUuid(document.getUuid());
            } else {
                String path = this.getTempPath();
                document.setPath(path + "/" + FileUtils.uniqueName(otherDocumentFile.getOtherDocument().getCustomerNumber()) + '.' + fileFormat);
                document = nodeDocumentService.createDocument(document, file.getInputStream());
                otherDocumentFileRepository.setFile(otherDocumentFile.getId(), document.getUuid());
                otherDocumentFileDto.setFileUuid(document.getUuid());
            }
        } else {
            throw badRequest("files must not be null");
        }
        OtherDocumentDto dto = otherDocumentMapper.toDto(otherDocumentFile.getOtherDocument());
        dto.getOtherDocumentFiles().add(otherDocumentFileDto);
        log.debug("Request to save otherDocument", dto);
        return dto;
    }


    @Override
    public OtherDocumentDto update(OtherDocumentDto otherDocumentDto) {
        log.debug("Request to update other Document : {}", otherDocumentDto);
        permissionService.checkPermission(ObjectName.OtherDocument, Collections.singletonList(otherDocumentDto.getId()));
        OtherDocument otherDocument = otherDocumentRepository.findById(otherDocumentDto.getId())
                .orElseThrow(() -> Responses.notFound("otherDocument not found"));
        if (!otherDocument.getLastState().getState().equals(OtherDocumentStateEnum.REGISTERED) &&
                !otherDocument.getLastState().getState().equals(OtherDocumentStateEnum.BRANCH_REJECTED))
            throw forbidden("Condition not valid");
        if (otherDocumentDto.getCustomerNumber() != null && !otherDocumentDto.getCustomerNumber().isEmpty())
            otherDocument.setCustomerNumber(otherDocumentDto.getCustomerNumber());
        if (otherDocumentDto.getFileNumber() != null && !otherDocumentDto.getFileNumber().isEmpty())
            otherDocument.setFileNumber(otherDocumentDto.getFileNumber());
        OtherDocumentDto dto = otherDocumentMapper.toDto(otherDocumentRepository.save(otherDocument));
        log.debug("Request to update otherDocument {}", dto);
        return dto;
    }

    @Transactional
    @Override
    public OtherDocumentDto findOne(Long id) {
        log.debug("Request to get other Document by id : {}", id);
        permissionService.checkPermission(ObjectName.OtherDocument, Collections.singletonList(id));
        OtherDocument otherDocument = otherDocumentRepository.findById(id)
                .orElseThrow(() -> notFound("otherDocument not found"));
        if (otherDocument.getLastState().getProfileSeen().stream().noneMatch(profile -> profile.getId().equals(currentUser().getId()))) {
            otherDocument.getLastState().getProfileSeen().add(currentUser());
            otherDocumentRepository.save(otherDocument);
        }
        OtherDocumentDto otherDocumentDto = otherDocumentMapper.toDto(otherDocument);
        List<OtherDocumentStateDto> documentRequestStates = otherDocumentStateRepository.findAllByOtherDocumentIdOrderByRegisterDate(id).stream().map(otherDocumentStateMapper::toDto).collect(Collectors.toList());
        otherDocumentDto.setStates(documentRequestStates);
        return otherDocumentDto;
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete other Document by id : {}", id);
        permissionService.checkPermission(ObjectName.OtherDocument, Collections.singletonList(id));
        Boolean checkRegistered = otherDocumentRepository.existsByIdAndLastState_State(id, OtherDocumentStateEnum.REGISTERED);
        if (checkRegistered != null && !checkRegistered)
            throw forbidden("Condition not valid");
        otherDocumentRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        log.debug("Request to delete other Document all by ids : {}", ids);
        permissionService.checkPermission(ObjectName.OtherDocument, ids);
        otherDocumentRepository.deleteAllById(ids);
    }

    @Override
    public OtherDocumentDto send(Long id) {
        log.debug("Request to send other Document : {}", id);
        permissionService.checkPermission(ObjectName.OtherDocument, Collections.singletonList(id));
        OtherDocument otherDocument = otherDocumentRepository.findById(id)
                .orElseThrow(() -> notFound("otherDocument not found"));
        if (otherDocument.getOtherDocumentFiles().stream().anyMatch(otherDocumentFile -> otherDocumentFile.getFile() != null)) {
            OtherDocumentStateEnum state = OtherDocumentStateEnum.nextState(otherDocument.getLastState().getState(), WorkflowOperationState.confirm);
            otherDocument.setLastState(new OtherDocumentState(state, otherDocument, null));
            otherDocumentRepository.save(otherDocument);
        } else {
            throw conflict("Other document incompletely registered");
        }
        return otherDocumentMapper.toDto(otherDocument);
    }

    public List<OtherDocumentDto> confirm(List<Long> otherDocumentIds, WorkflowOperationState operation, String description) {
        log.debug("Request to confirm otherDocument : {}", otherDocumentIds, operation);
        permissionService.checkPermission(ObjectName.OtherDocument, otherDocumentIds);
        try {
            List<OtherDocument> otherDocuments = otherDocumentRepository.findAllByIdIn(otherDocumentIds);
            otherDocuments.forEach(otherDocument -> {
                OtherDocumentStateEnum state = OtherDocumentStateEnum.nextState(otherDocument.getLastState().getState(), operation);
                otherDocument.setLastState(new OtherDocumentState(state, otherDocument, description));
                otherDocumentRepository.save(otherDocument);
                otherDocument.getOtherDocumentFiles().forEach(otherDocumentFile -> {
                    String path = this.createPath(otherDocument, otherDocumentFile.getOtherDocumentType());
                    nodeDocumentService.moveDocument(otherDocumentFile.getFile().getUuid(), path);

                });
            });
            return otherDocumentMapper.toDto(otherDocuments);
        } catch (Exception e) {
            log.error("complete otherDocument : " + otherDocumentIds + " " + e.getMessage());
            throw Responses.forbidden("otherDocument not completed");
        }
    }

    @Override
    public List<OtherDocumentDto> complete(List<Long> otherDocumentIds, WorkflowOperationState operation, String description) {
        log.debug("Request to complete otherDocument : {}", otherDocumentIds, operation);
        permissionService.checkPermission(ObjectName.OtherDocument, otherDocumentIds);
        try {
            if (operation.equals(WorkflowOperationState.reject) && (description == null || description.isEmpty()))
                throw Responses.forbidden("description can not be null");
            List<OtherDocument> otherDocuments = otherDocumentRepository.findAllByIdIn(otherDocumentIds);
            otherDocuments.forEach(otherDocument -> {
                OtherDocumentStateEnum state = OtherDocumentStateEnum.nextState(otherDocument.getLastState().getState(), operation);
                otherDocument.setLastState(new OtherDocumentState(state, otherDocument, description));
                otherDocumentRepository.save(otherDocument);
            });
            return otherDocumentMapper.toDto(otherDocuments);
        } catch (Exception e) {
            log.error("complete otherDocument : " + otherDocumentIds + " " + e.getMessage());
            throw Responses.forbidden("otherDocument not completed");
        }
    }


    @Override
    public Page<OtherDocumentDto> advanceSearch(
            AdvanceOtherDocumentSearchDto advanceOtherDocumentSearchDto,
            Pageable pageable) {
        log.debug("Request to advanceSearch {}:", advanceOtherDocumentSearchDto);
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        RoleEnum role = profileRepository.getRole(profile.getId());
        if (role.equals(BU)) {
            advanceOtherDocumentSearchDto.setBranchIds(Collections.singletonList(profile.getBranch().getId()));
        } else if (role.equals(BA) && (advanceOtherDocumentSearchDto.getBranchIds() == null || advanceOtherDocumentSearchDto.getBranchIds().isEmpty()))
            advanceOtherDocumentSearchDto.setBranchIds(branchRepository.getAllByParentId(profile.getBranch().getId()));
        return otherDocumentRepository.advanceSearch(
                advanceOtherDocumentSearchDto.getRegisterFromDate() != null ? toDate(advanceOtherDocumentSearchDto.getRegisterFromDate()) : null,
                advanceOtherDocumentSearchDto.getRegisterToDate() != null ? toDate(advanceOtherDocumentSearchDto.getRegisterToDate()) : null,
                advanceOtherDocumentSearchDto.getCustomerNumber(),
                advanceOtherDocumentSearchDto.getFileNumber(),
                advanceOtherDocumentSearchDto.getFileTypeId(),
                advanceOtherDocumentSearchDto.getOtherDocumentTypeId(),
                advanceOtherDocumentSearchDto.getFileStatusId(),
                advanceOtherDocumentSearchDto.getRegistrarId(),
                advanceOtherDocumentSearchDto.getBranchIds(),
                advanceOtherDocumentSearchDto.getState(),
                pageable
        ).map(otherDocumentMapper::toDto);
    }

    private String getTempPath() {
        if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolderTemp))
            folderService.createFolder(OpenKM.otherDocumentFolderTemp);
        return OpenKM.rootPath + OpenKM.otherDocumentFolderTemp;
    }

    private String createPath(OtherDocument otherDocument, OtherDocumentType otherDocumentType) {

        if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolderPath))
            folderService.createFolder(OpenKM.otherDocumentFolder);

        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        Branch branch = profile.getBranch();
        String path = branch.getPath();
        Long branchCode = branch.getBranchCode();
        String branchName = branch.getBranchName();
        String cityName = branch.getCityName() != null && !branch.getCityName().isEmpty() ? branch.getCityName() : "";
        String superVisor = branch.getSuperVisorCode() != null && !branch.getSuperVisorCode().isEmpty() ? branch.getSuperVisorCode() : "";

        if (path == null || path.isEmpty()) {
            if (superVisor != null && !superVisor.isEmpty()) {
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolderPath + superVisor))
                    folderService.createFolder(OpenKM.otherDocumentFolderPath + superVisor);
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolderPath + superVisor + "/" + branchName + "-" + branchCode))
                    folderService.createFolder(OpenKM.otherDocumentFolderPath + superVisor + "/" + branchName + "-" + branchCode);

                path = OpenKM.rootPath + OpenKM.otherDocumentFolderPath + superVisor + "/" + branchName + "-" + branchCode;
            } else {
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolderPath + branchName + "-" + branchCode))
                    folderService.createFolder(OpenKM.otherDocumentFolderPath + branchName + "-" + branchCode);
                path = OpenKM.rootPath + OpenKM.otherDocumentFolderPath + branchName + "-" + branchCode;
            }
        }

        if (path.contains("province"))
            path = path.replace("province", "otherDocument");

        if (!folderService.isValidFolder(path + "/" + otherDocument.getCustomerNumber())) {
            folderService.createFolderWithPath(path + "/" + otherDocument.getCustomerNumber());

        }
        path = path + "/" + otherDocument.getCustomerNumber();
        if (otherDocument.getFileNumber() != null && !otherDocument.getFileNumber().isEmpty()) {
            if (!folderService.isValidFolder(path + "/" + otherDocument.getFileNumber())) {
                folderService.createFolderWithPath(path + "/" + otherDocument.getFileNumber());

            }
            path = path + "/" + otherDocument.getFileNumber();
        }
        if (!folderService.isValidFolder(path + "/" + otherDocument.getFileType().getTitle())) {
            folderService.createFolderWithPath(path + "/" + otherDocument.getFileType().getTitle());
        }
        path = path + "/" + otherDocument.getFileType().getTitle();
        if (!folderService.isValidFolder(path + "/" + otherDocumentType.getTitle())) {
            folderService.createFolderWithPath(path + "/" + otherDocumentType.getTitle());
        }
        path = path + "/" + otherDocumentType.getTitle();
        return path;
    }
}
