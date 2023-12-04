package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.document.DocumentConflictDto;
import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.dto.document.DocumentStateDto;
import com.sima.dms.domain.dto.request.AdvanceDocumentSearchDto;
import com.sima.dms.domain.dto.request.FixConflictDocumentRequestDto;
import com.sima.dms.domain.dto.request.SetConflictDocumentRequestDto;
import com.sima.dms.domain.dto.request.UpdateDocumentRequestDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.document.Document;
import com.sima.dms.domain.entity.document.DocumentConflict;
import com.sima.dms.domain.entity.document.DocumentState;
import com.sima.dms.domain.entity.documentSet.DocumentSet;
import com.sima.dms.domain.enums.*;
import com.sima.dms.repository.*;
import com.sima.dms.service.DocumentService;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.NodeDocumentService;
import com.sima.dms.service.PermissionService;
import com.sima.dms.service.mapper.ConflictReasonMapper;
import com.sima.dms.service.mapper.DocumentConflictMapper;
import com.sima.dms.service.mapper.DocumentMapper;
import com.sima.dms.service.mapper.DocumentStateMapper;
import com.sima.dms.tools.FileUtils;
import com.sima.dms.tools.PDFSigner;
import com.sima.dms.constants.OpenKM;
import com.sima.dms.utils.DateUtils;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sima.dms.constants.OpenKM.*;
import static com.sima.dms.constants.OpenKM.documentSetPath;
import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.enums.DocumentStateEnum.*;
import static com.sima.dms.domain.enums.RoleEnum.RU;
import static com.sima.dms.domain.enums.WorkflowOperation.expired;
import static com.sima.dms.domain.enums.WorkflowOperation.*;
import static com.sima.dms.utils.Responses.badRequest;
import static com.sima.dms.utils.Responses.notFound;
import static java.util.Objects.isNull;

@Service
//@Transactional
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final FolderService folderService;
    private final NodeDocumentService nodeDocumentService;
    private final PermissionService permissionService;

    private final DocumentMapper documentMapper;
    private final DocumentStateMapper documentStateMapper;
    private final DocumentConflictMapper documentConflictMapper;
    private final ConflictReasonMapper documentConflictReasonMapper;

    private final BranchRepository branchRepository;
    private final ProfileRepository profileRepository;
    private final DocumentRepository documentRepository;
    private final DocumentSetRepository documentSetRepository;
    private final DocumentStateRepository documentStateRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final NodeDocumentRepository nodeDocumentRepository;


    private final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public DocumentDto save(DocumentDto documentDto) {
        Document document = documentMapper.toEntity(documentDto);

        DocumentSet documentSet = documentSetRepository.findById(documentDto.getDocumentSetId())
                .orElseThrow(() -> Responses.notFound("DocumentSet not found"));
        document.setDocumentSet(documentSet);
        document.setState(new DocumentState(DocumentStateEnum.NOT_CHECKED, null, null));
        Document documentInfo = documentRepository.save(document);
        documentDto = documentMapper.toDto(documentInfo);
        log.debug("Request to save Document : {}", documentDto);
        return documentDto;
    }

    @Override
    public void update(UpdateDocumentRequestDto updateDocumentRequestDto) {
        log.debug("Request to update Document : {}", updateDocumentRequestDto);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(updateDocumentRequestDto.getDocumentId()));
        documentRepository.updateMaintenanceCode(updateDocumentRequestDto.getDocumentId(), updateDocumentRequestDto.getMaintenanceCode());
    }

    @Override
    public DocumentDto getById(Long id) {
        log.debug("Request to get Document : {}", id);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(id));
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> Responses.notFound("Document  not found"));

        if (document.getState().getProfileSeen().stream().noneMatch(profile -> profile.getId().equals(currentUser().getId()))) {
            document.getState().getProfileSeen().add(currentUser());
            documentRepository.save(document);
        }
        return documentMapper.toDto(document);
    }

    @Override
    public Page<DocumentDto> getAll(Pageable pageable) {
        log.debug("Request to get all Document : {}");
        return documentRepository.findAll(pageable)
                .map(documentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Document : {}", id);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(id));
        documentRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        log.debug("Request to delete Documents : {}", ids);
        permissionService.checkPermission(ObjectName.Document, ids);
        documentRepository.deleteAllById(ids);
    }


    @Override
    public Page<DocumentDto> getDocumentsByStatesAndBranchIds(List<DocumentStateEnum> states, Pageable pageable) {
        log.debug("Request to get Documents by states: {}", states);
        Long currentUserId = currentUser().getId();
        List<Long> branchIds = branchRepository.getAssignBranches(currentUserId);
        RoleEnum role = profileRepository.getRole(currentUserId);
        if ((isNull(branchIds) || branchIds.isEmpty()) && (role.equals(RoleEnum.DOA) || role.equals(RoleEnum.ADMIN))) {
            return documentRepository.findAllByState_NameIn(states, pageable).map(documentMapper::toDto);
        } else
            return documentRepository.findAllByState_NameInAndDocumentSet_Branch_IdIn(states, branchIds, pageable).map(documentMapper::toDto);
    }

    @Override
    public Page<DocumentDto> getByStateAndBranchId(DocumentStateEnum state, Long branchId, Pageable pageable) {
        log.debug("Request to get Documents by state and branchId: {}", state, branchId);
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        Page<DocumentDto> pages = null;
        if (profile.getRole().equals(RoleEnum.ADMIN))
            pages = documentRepository.findAllByState_Name(state, pageable)
                    .map(documentMapper::toDto);
        else if (profile.getRole().equals(RoleEnum.BA))
            pages = documentRepository.findAllByState_NameAndDocumentSet_Branch_IdIn(state, branchRepository.getAllByParentId(profile.getBranch().getId()), pageable).map(documentMapper::toDto);
        else if (profile.getRole().equals(RoleEnum.BU))
            pages = documentRepository.findAllByState_NameAndDocumentSet_Branch_IdIn(state, Collections.singletonList(branchId), pageable).map(documentMapper::toDto);
        return pages;
    }

    @Override
    public List<DocumentDto> getAllByDocumentState(Long documentSetId, DocumentStateEnum state) {
        log.debug("Request to get By DocumentState : {}{}", documentSetId, state);
        return documentMapper.toDto(documentRepository.getAllByDocumentState(documentSetId, state));
    }

    @Override
    public Page<DocumentDto> conflictingManagement(AdvanceDocumentSearchDto searchDto, Pageable pageable) {
        log.debug("Request to get Documents for conflicting Management: {}", searchDto);
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        RoleEnum role = profile.getRole();
        if (role.equals(RoleEnum.ADMIN) || role.equals(RoleEnum.DOA))
            return documentRepository.findAllConflictingManagementAdminAndDoa(searchDto.getBranchIds(),
                    searchDto.getMaintenanceCode(),
                    searchDto.getStates() != null && !searchDto.getStates().isEmpty() ? searchDto.getStates().stream().map(Enum::ordinal).collect(Collectors.toList()) : null,
                    searchDto.getFromDate() != null ? DateUtils.toDate(searchDto.getFromDate()) : null,
                    searchDto.getToDate() != null ? DateUtils.toDate(searchDto.getToDate()) : null,
                    searchDto.getRegisterFromDate() != null ? DateUtils.toDate(searchDto.getRegisterFromDate()) : null,
                    searchDto.getRegisterToDate() != null ? DateUtils.toDate(searchDto.getRegisterToDate()) : null,
                    searchDto.getFilename(),
                    searchDto.getType() != null ? searchDto.getType().ordinal() : null,
                    searchDto.getConflictRegisterDate() != null ? DateUtils.toDate(searchDto.getConflictRegisterDate()) : null,
                    searchDto.getReason() != null ? searchDto.getReason() : null,
                    searchDto.getCustomerNumber(),
                    searchDto.getFileNumber(),
                    searchDto.getFileStatusId(),
                    searchDto.getFileTypeId(),
                    pageable).map(documentMapper::toDto);

        if ((searchDto.getBranchIds() == null || searchDto.getBranchIds().isEmpty()) && role.equals(RoleEnum.DOEU))
            searchDto.setBranchIds(profile.getAssignedBranches().stream().map(Branch::getId).collect(Collectors.toList()));
        return documentRepository.findAllConflictingManagement(searchDto.getBranchIds(),
                searchDto.getMaintenanceCode(),
                searchDto.getStates() != null && !searchDto.getStates().isEmpty() ? searchDto.getStates().stream().map(Enum::ordinal).collect(Collectors.toList()) : null,
                searchDto.getFromDate() != null ? DateUtils.toDate(searchDto.getFromDate()) : null,
                searchDto.getToDate() != null ? DateUtils.toDate(searchDto.getToDate()) : null,
                searchDto.getRegisterFromDate() != null ? DateUtils.toDate(searchDto.getRegisterFromDate()) : null,
                searchDto.getRegisterToDate() != null ? DateUtils.toDate(searchDto.getRegisterToDate()) : null,
                searchDto.getFilename(),
                searchDto.getType() != null ? searchDto.getType().ordinal() : null,
                searchDto.getConflictRegisterDate() != null ? DateUtils.toDate(searchDto.getConflictRegisterDate()) : null,
                searchDto.getReason() != null ? searchDto.getReason() : null,
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                pageable).map(documentMapper::toDto);
    }

    @Override
    public List<DocumentStateDto> getDocumentHistory(Long documentId) {
        log.debug("Request to get Document history: {}", documentId);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(documentId));
        return documentStateMapper.toDto(documentStateRepository.findAllByDocumentId((documentId)));
    }

    @Override
    public List<DocumentDto> primaryConfirmDocument(List<Long> documentIds) {
        log.debug("Request to primary confirm Document: {}", documentIds);
        permissionService.checkPermission(ObjectName.Document, documentIds);
        List<Document> documents = documentRepository.findAllByIdIn(documentIds);
        if (documents.stream().anyMatch(document -> document.getMaintenanceCode() == null || document.getMaintenanceCode().isEmpty()))
            throw Responses.forbidden("maintenance code must be full");
        try {
            documents.forEach(document -> {
                if (document.getState().getName().equals(DocumentStateEnum.NOT_CHECKED)) {
                    complete(document, confirm, null);
                } else {
                    log.error("primary confirm Document  : ", document);
                    throw Responses.conflict("document should be in not checked state");
                }
            });
            documentRepository.saveAll(documents);
            return documentMapper.toDto(documents);
        } catch (Exception e) {
            log.error("primary confirm Document  : ", documentIds);
            throw Responses.forbidden("error in primary confirm document");
        }
    }

    @Override
    public DocumentDto setConflict(SetConflictDocumentRequestDto requestDto) {
        log.debug("Request to set conflict to Document : {}", requestDto);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(requestDto.getDocumentId()));
        Document document = documentRepository.findById(requestDto.getDocumentId())
                .orElseThrow(() -> Responses.notFound("Document not found"));

        if (document.getState().getName().equals(DocumentStateEnum.NOT_CHECKED)) {

            DocumentConflictDto conflict = new DocumentConflictDto();
            conflict.setRegisterDescription(requestDto.getDescription());

            conflict.setConflictReasons(requestDto.getConflictReasons().stream().map(documentConflictReasonMapper::dtoFormId).collect(Collectors.toList()));

            document.setConflicts(Collections.singletonList(documentConflictMapper.toEntity(conflict)));
            complete(document, conflicting, null);
            return documentMapper.toDto(documentRepository.save(document));

        } else {
            log.error("Set Document conflict : " + requestDto);
            throw Responses.conflict("Document should be in not checked state");
        }
    }

    @Override
    public DocumentDto fixConflict(FixConflictDocumentRequestDto requestDto) {

        log.debug("Request to fix Document conflict: {}", requestDto);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(requestDto.getDocumentId()));
        Document document = documentRepository.findById(requestDto.getDocumentId())
                .orElseThrow(() -> Responses.notFound("Document not found"));

        if (document.getConflicts() != null && !document.getConflicts().isEmpty()) {

            Optional<DocumentConflict> conflict = document.getConflicts().stream().filter(c -> c.getResolver() == null).findFirst();

            conflict.ifPresent(c -> {

                if (c.getDocument().getState().getName().equals(SENT_CONFLICT)) {
                    c.setResolvingDate(Instant.now());
                    c.setResolveDescription(requestDto.getDescription());
                    complete(document, confirm, null);
                    documentRepository.save(document);

                } else {
                    log.error("Document state is not conflicting : " + requestDto);
                    throw Responses.conflict("Document should be in conflicting sent conflict");
                }
            });
        } else {
            log.error("conflict not found : " + requestDto);
            throw Responses.notFound("The document has no conflict");
        }
        return documentMapper.toDto(document);

    }

    @Override
    public void rescan(Long documentId, MultipartFile content) throws IOException {

        log.debug("Request to rescan Document : {}", documentId);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(documentId));
        Tika tika = new Tika();
        String mimeType = tika.detect(content.getInputStream());
        if (!mimeType.equals("image/tif") && !mimeType.equals("application/pdf")) {
            throw Responses.badRequest("The file mimeType must be tif or pdf");
        }
        String extension = FilenameUtils.getExtension(content.getOriginalFilename());
        if (!extension.equals("tif") && !extension.equals("pdf")) {
            throw Responses.badRequest("The file format must be tif or pdf");
        }

        if (documentRepository.findStateName(documentId).equals(DocumentStateEnum.CONFIRM_FIX_CONFLICT)) {

            final String fileUUuid = documentRepository.findFileUUuid(documentId);
            if (fileUUuid != null) {
                nodeDocumentService.updateDocument(fileUUuid, content.getInputStream());
                documentRepository.updateProcessState(fileUUuid, ProcessStateEnum.PENDING);
                complete(documentId, null, null);
            }
        } else {
            log.error("Set Document conflict : " + documentId);
            throw Responses.conflict("Document should be in not checked state");
        }
    }

    @Override
    public void deleteDocumentConflicts(Long documentId) {
        log.debug("Request to delete Document conflicts: {}", documentId);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(documentId));
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> Responses.notFound("Document not found"));

        if (document.getState().getName().equals(DocumentStateEnum.CONFLICTING)) {
            document.setConflicts(null);
            complete(document, reject, null);
            documentRepository.save(document);
        } else {
            log.error("Fix Document conflict   : " + documentId);
            throw Responses.conflict("Document should be in conflicting state");
        }
    }

    @Override
    public Page<DocumentDto> advanceSearch(
            String maintenanceCode,
            List<DocumentStateEnum> states,
            String fromDate,
            String toDate,
            String registerFromDate,
            String registerToDate,
            List<Long> branchIds,
            String filename,
            String documentNumber,
            String documentDate,
            String reason,
            String rowNumber,
            DocumentSetTypeEnum type,
            String customerNumber,
            String fileNumber,
            Long fileStatusId,
            Long fileTypeId,
            Pageable pageable) {

        log.debug("Request to search document : {}");
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        RoleEnum role = profileRepository.getRole(profile.getId());
        if (role.equals(RoleEnum.ADMIN) || role.equals(RoleEnum.DOA) || role.equals(RU))
            return documentRepository.advanceSearchAdminAndDoa(branchIds,
                    maintenanceCode,
                    states,
                    fromDate != null ? DateUtils.toDate(fromDate) : null,
                    toDate != null ? DateUtils.toDate(toDate) : null,
                    registerFromDate != null ? DateUtils.toDate(registerFromDate) : null,
                    registerToDate != null ? DateUtils.toDate(registerToDate) : null,
                    filename,
                    documentNumber, documentDate, reason, rowNumber, type,
                    customerNumber,
                    fileNumber,
                    fileStatusId,
                    fileTypeId,
                    pageable).map(documentMapper::toDto);

        if (role.equals(RoleEnum.BU) || role.equals(RoleEnum.BA)) {

            branchIds = Collections.singletonList(profile.getBranch().getId());
        } else if (role.equals(RoleEnum.DOPU) || role.equals(RoleEnum.DOEU)) {
            if (branchIds == null || branchIds.isEmpty())
                branchIds = profile.getAssignedBranches().stream().map(Branch::getId).collect(Collectors.toList());
        }

        return documentRepository.advanceSearch(branchIds,
                maintenanceCode,
                states,
                fromDate != null ? DateUtils.toDate(fromDate) : null,
                toDate != null ? DateUtils.toDate(toDate) : null,
                registerFromDate != null ? DateUtils.toDate(registerFromDate) : null,
                registerToDate != null ? DateUtils.toDate(registerToDate) : null,
                filename,
                documentNumber, documentDate, reason, rowNumber, type,
                customerNumber,
                fileNumber,
                fileStatusId,
                fileTypeId,
                pageable).map(documentMapper::toDto);
    }

    @Override
    public List<DocumentDto> toStagnateDocument(List<Long> ids) {
        log.debug("Request to Stagnate Confirm Document : {}", ids);
        permissionService.checkPermission(ObjectName.Document, ids);
        List<Document> documents = documentRepository.findAllByIdIn(ids);
        try {
            documents.forEach(document -> {
                if (document.getState().getName().equals(PRIMARY_CONFIRMED)) {
                    toStagnateDocumentAndConflictDocument(document, confirm);
                    try {
                        InputStream signedPDF = PDFSigner.signPDF(nodeDocumentService.getContent(document.getFile().getUuid()));
                        nodeDocumentService.updateDocument(document.getFile().getUuid(), signedPDF);
                    } catch (IOException e) {
                        throw Responses.forbidden(e.getMessage());
                    }
                } else if (document.getState().getName().equals(CONFLICTING))
                    complete(document, confirm, null);
                else {
                    log.error("to Stagnate Document : ", document);
                    throw Responses.conflict("Document should be in sent conflict state");
                }
            });
            documentRepository.saveAll(documents);
            return documentMapper.toDto(documents);
        } catch (Exception e) {
            log.error("to Stagnate Document  : ", ids);
            throw Responses.forbidden("error in stagnate document");
        }
    }

    @Override
    public List<DocumentDto> toStagnateConflictDocument(List<Long> ids) {
        log.debug("Request to Stagnate Confirm Conflict Document : {}", ids);
        permissionService.checkPermission(ObjectName.Document, ids);
        List<Document> documents = documentRepository.findAllByIdIn(ids);
        try {
            documents.forEach(document -> {
                if (document.getState().getName().equals(SENT_CONFLICT)) {
                    toStagnateDocumentAndConflictDocument(document, expired);
                    try {
                        InputStream signedPDF = PDFSigner.signPDF(nodeDocumentService.getContent(document.getFile().getUuid()));
                        nodeDocumentService.updateDocument(document.getFile().getUuid(), signedPDF);
                    } catch (IOException e) {
                        throw Responses.forbidden(e.getMessage());
                    }
                } else {
                    log.error("to Stagnate Conflict Document : ", document);
                    throw Responses.conflict("Document should be in sent conflict state");
                }
            });
            documentRepository.saveAll(documents);
            return documentMapper.toDto(documents);
        } catch (Exception e) {
            log.error("to Stagnate Conflict Document  : ", ids);
            throw Responses.forbidden("error in stagnate document");
        }
    }

    @Override
    public void updateDocument(Long documentSetId, String uuid, MultipartFile content) throws IOException {
        log.debug("Request to update document : {}", uuid, content);
        if (content == null || content.isEmpty())
            throw badRequest("files must not be null");
        Tika tika = new Tika();
        String mimeType = tika.detect(content.getInputStream());
        if (!mimeType.equals("image/tif") && !mimeType.equals("application/pdf") && !mimeType.equals("image/tiff")) {
            throw Responses.badRequest("The file mimeType must be tif or pdf");
        }
        String extension = FilenameUtils.getExtension(content.getOriginalFilename());
        if (!extension.equals("tif") && !extension.equals("pdf") && !extension.equals("tiff")) {
            throw Responses.badRequest("The file format must be tif or pdf");
        }
        NodeDocumentDto nodeDocument = nodeDocumentService.findByUuid(uuid);

        if (mimeType.equals(nodeDocument.getMimeType())) {
            nodeDocumentService.updateDocument(uuid, content.getInputStream());
        } else {

            DocumentSet documentSet = documentSetRepository.findById(documentSetId)
                    .orElseThrow(() -> Responses.notFound("request not found"));
            Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
            Branch branch = profile.getBranch();
            Long branchCode = branch.getBranchCode();
            String branchName = branch.getBranchName();
            String folder = documentSet.getRowsNumber() + documentSet.getSequence();

            if (!folderService.isValidFolder(rootPath + documentSetPath))
                folderService.createFolder(documentSetFolder);

            if (!folderService.isValidFolder(rootPath + documentSetPath + branchName + "-" + branchCode))
                folderService.createFolder(documentSetPath + branchName + "-" + branchCode);

            if (!folderService.isValidFolder(rootPath + documentSetPath + branchName + "-" + branchCode + "/" + folder))
                folderService.createFolder(documentSetPath + branchName + "-" + branchCode + "/" + folder);

            com.openkm.sdk4j.bean.Document document = new com.openkm.sdk4j.bean.Document();
            String fileName = FileNameUtils.getBaseName(documentRepository.getFileNameByUuid(uuid)) + '.' + FileNameUtils.getExtension(content.getOriginalFilename());
            document.setPath(rootPath + documentSetPath + branchName + "-" + branchCode + "/" + folder + "/" + FileUtils.getInstant() + FileUtils.getFormat(content.getOriginalFilename()));
            document = nodeDocumentService.createDocument(document, content.getInputStream());

            documentRepository.updateUuidAndFileName(nodeDocument.getUuid(), document.getUuid(), fileName);
            nodeDocumentService.deleteDocument(nodeDocument.getUuid());
            nodeBaseRepository.deleteByUuid(nodeDocument.getUuid());
        }

    }


    @Override
    public DocumentDto complete(Long id, WorkflowOperation operation, String description) {
        log.debug("Request to complete Document : {}", id, operation);
        permissionService.checkPermission(ObjectName.Document, Collections.singletonList(id));
        try {

            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> Responses.notFound("Document not found"));
            DocumentStateEnum nextState = documentNextStep(document.getState().getName(), operation);
            DocumentState newState = new DocumentState(nextState, document.getState(), description);
            document.setState(newState);
            document.addStates(Collections.singletonList(newState));
            return documentMapper.toDto(documentRepository.save(document));

        } catch (Exception e) {
            log.error("complete Document : " + id + " " + e.getMessage());
            throw Responses.forbidden("Document not completed");
        }
    }

    @Override
    public List<DocumentDto> complete(List<Long> ids, WorkflowOperation operation, String description) {
        log.debug("Request to complete Documents : {}", ids, operation);
        permissionService.checkPermission(ObjectName.Document, ids);
        try {
            List<Document> documents = documentRepository.findAllByIdIn(ids);
            documents.forEach(document -> {
                DocumentStateEnum nextState = documentNextStep(document.getState().getName(), operation);
                DocumentState newState = new DocumentState(nextState, document.getState(), description);
                document.setState(newState);
                document.addStates(Collections.singletonList(newState));
            });
            documentRepository.saveAll(documents);
            return documentMapper.toDto(documents);

        } catch (Exception e) {
            log.error("complete Documents : " + ids + " " + e.getMessage());
            throw Responses.forbidden("Document not completed");
        }
    }

    private void complete(Document document, WorkflowOperation operation, String description) {
        log.debug("Request to complete Document  : {}", document, operation);
        try {
            DocumentStateEnum nextState = documentNextStep(document.getState().getName(), operation);
            DocumentState newState = new DocumentState(nextState, document.getState(), description);
            document.setState(newState);
            document.addStates(Collections.singletonList(newState));
        } catch (Exception e) {
            log.error("complete Document : " + document + " " + e.getMessage());
            throw Responses.forbidden("Document not completed");
        }
    }

    private void toStagnateDocumentAndConflictDocument(Document document, WorkflowOperation operation) {
        complete(document, operation, null);
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        Branch branch = profile.getBranch();
        Long branchCode = branch.getBranchCode();
        String branchName = branch.getBranchName();
        String cityName = branch.getCityName() != null && !branch.getCityName().isEmpty() ? branch.getCityName() : "";
        String folder = document.getDocumentSet().getRowsNumber() + document.getDocumentSet().getSequence();

        String path = branch.getPath();
        if (path == null || path.isEmpty()) {
            if (cityName != null && !cityName.isEmpty()) {
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.provinceFolder))
                    folderService.createFolderSimple(OpenKM.provinceFolder);
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.provincePath + cityName))
                    folderService.createFolderSimple(OpenKM.provincePath + cityName);
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.provincePath + cityName + "/" + branchName + "-" + branchCode))
                    folderService.createFolderSimple(OpenKM.provincePath + cityName + "/" + branchName + "-" + branchCode);

                path = OpenKM.rootPath + OpenKM.provincePath + cityName + "/" + branchName + "-" + branchCode;
            } else {
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.provinceFolder))
                    folderService.createFolderSimple(OpenKM.provinceFolder);
                if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.provincePath + branchName + "-" + branchCode))
                    folderService.createFolderSimple(OpenKM.provincePath + branchName + "-" + branchCode);
                path = OpenKM.rootPath + OpenKM.provincePath + branchName + "-" + branchCode;
            }
        }
        if (!folderService.isValidFolder(path + "/" + OpenKM.yearFolder))
            folderService.createFolderWithPath(path + "/" + OpenKM.yearFolder);

        if (!folderService.isValidFolder(path + "/" + OpenKM.yearPatch + OpenKM.monthFolder))
            folderService.createFolderWithPath(path + "/" + OpenKM.yearPatch + OpenKM.monthFolder);

        if (!folderService.isValidFolder(path + "/" + OpenKM.yearPatch + OpenKM.monthPatch + OpenKM.dayFolder))
            folderService.createFolderWithPath(path + "/" + OpenKM.yearPatch + OpenKM.monthPatch + OpenKM.dayFolder);

        if (!folderService.isValidFolder(path + "/" + OpenKM.yearPatch + OpenKM.monthPatch + OpenKM.dayPatch + folder))
            folderService.createFolderWithPath(path + "/" + OpenKM.yearPatch + OpenKM.monthPatch + OpenKM.dayPatch + folder);

        if (document.getFile() != null) {
            nodeDocumentService.moveDocument(document.getFile().getUuid(), path + "/" + OpenKM.yearPatch + OpenKM.monthPatch + OpenKM.dayPatch + folder);
            nodeDocumentRepository.updateTextExtracted(document.getFile().getUuid(), true);
        }
    }
}
