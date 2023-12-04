package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.dto.document.DocumentStateDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetConflictDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetStateDto;
import com.sima.dms.domain.dto.OcrDocumentReportDto;
import com.sima.dms.domain.dto.request.FixConflictRequestDto;
import com.sima.dms.domain.dto.request.SetAllConflictRequestDto;
import com.sima.dms.domain.dto.request.SetConflictRequestDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.document.Document;
import com.sima.dms.domain.entity.documentSet.DocumentSet;
import com.sima.dms.domain.entity.documentSet.DocumentSetConflict;
import com.sima.dms.domain.entity.documentSet.DocumentSetState;
import com.sima.dms.domain.enums.*;
import com.sima.dms.repository.*;
import com.sima.dms.service.DocumentSetService;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.NodeDocumentService;
import com.sima.dms.service.PermissionService;
import com.sima.dms.service.mapper.*;
import com.sima.dms.tools.FileUtils;
import com.sima.dms.tools.JalaliCalendar;
import com.sima.dms.tools.UnzipFile;
import com.sima.dms.utils.DateUtils;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.sima.dms.constants.OpenKM.*;
import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.entity.session.Authorized.currentUserId;
import static com.sima.dms.domain.enums.DocumentSetStateEnum.*;
import static com.sima.dms.domain.enums.DocumentStateEnum.CONFLICTED_STAGNANT;
import static com.sima.dms.domain.enums.DocumentStateEnum.STAGNANT;
import static com.sima.dms.domain.enums.RoleEnum.*;
import static com.sima.dms.domain.enums.WorkflowOperation.confirm;
import static com.sima.dms.domain.enums.WorkflowOperation.conflicting;
import static com.sima.dms.utils.Responses.*;
import static java.util.Objects.isNull;

@Service
@EnableAsync
@Transactional
@AllArgsConstructor
public class DocumentSetServiceImpl implements DocumentSetService {

    private final FolderService folderService;
    private final NodeDocumentService nodeDocumentService;
    private final PermissionService permissionService;
    private final NodeDocumentRepository nodeDocumentRepository;

    private final DocumentMapper documentMapper;
    private final DocumentSetMapper documentSetMapper;
    private final DocumentStateMapper documentStateMapper;
    private final DocumentSetStateMapper documentSetStateMapper;
    private final DocumentSetConflictMapper documentSetConflictMapper;
    private final ConflictReasonMapper conflictReasonMapper;
    private final FileStatusMapper fileStatusMapper;

    private final BranchRepository branchRepository;
    private final ProfileRepository profileRepository;
    private final DocumentRepository documentRepository;
    private final DocumentSetRepository documentSetRepository;
    private final DocumentSetStateRepository documentSetStateRepository;
    private final NodeBaseRepository nodeBaseRepository;

    private final Logger log = LoggerFactory.getLogger(DocumentSetServiceImpl.class);

    @Override
    @Transactional
    public DocumentSetDto save(DocumentSetDto documentSetDto) {
        if (documentSetDto.getType().equals(DocumentSetTypeEnum.DAILY) || documentSetDto.getType().equals(DocumentSetTypeEnum.CHAKAVAK)) {
            if (documentSetDto.getFromDate() == null || documentSetDto.getToDate() == null)
                throw Responses.forbidden("documentSetDate not be null");
            if (documentSetDto.getFromDate().isAfter(documentSetDto.getToDate()))
                throw badRequest("the toDate must not be earlier than the fromDate");
        } else {
            if (documentSetDto.getCustomerNumber() == null || documentSetDto.getCustomerNumber().isEmpty())
                throw Responses.forbidden("Customer Number not be null");
            else if (documentSetDto.getFileNumber() == null || documentSetDto.getFileNumber().isEmpty())
                throw Responses.forbidden("file Number not be null");
            else if (documentSetDto.getFileStatusId() == null) throw Responses.forbidden("file status not be null");
        }
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        Branch branch = profile.getBranch();
        DocumentSet documentSet = documentSetMapper.toEntity(documentSetDto);
        documentSet.setState(new DocumentSetState(REGISTERED));
        documentSet.addStates(Collections.singletonList(documentSet.getState()));
        documentSet.setRowsNumber(generateRowNumber(documentSetDto.getType(), branch.getBranchCode()));
        documentSet.setBranch(profile.getBranch());
        Integer maxSequence = documentSetRepository.getMaxSequenceByBranchId(branch.getId());

        if (maxSequence != null) documentSet.setSequence(String.valueOf((maxSequence + 1)));
        else documentSet.setSequence(String.valueOf((1)));

        documentSet = documentSetRepository.save(documentSet);

        documentSetDto = documentSetMapper.toDto(documentSet);
        log.debug("Request to save DocumentSet : {}", documentSetDto);
        return documentSetDto;
    }

    @Override
    @Transactional
    public DocumentSetDto update(DocumentSetDto documentSetDto) {

        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetDto.getId()));

        DocumentSet documentSet = documentSetRepository.findById(documentSetDto.getId()).orElseThrow(() -> notFound("DocumentSet not found"));

        if (documentSet.getState().getName().equals(BRANCH_CONFIRMED))
            throw conflict("in the branch confirmed status , documentSet cannot be changed");

        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        if (profile.getRole().equals(ADMIN) || profile.getId().equals(documentSet.getCreatedBy().getId()) || ((documentSet.getBranch().getId().equals(profile.getBranch().getId()) || documentSet.getBranch().getParent().getId().equals(profile.getBranch().getId())) && profile.getRole().equals(BA))) {

            if (documentSetDto.getFromDate().isAfter(documentSetDto.getToDate()))
                throw badRequest("the toDate must not be earlier than the fromDate");
            if (documentSetDto.getFromDate() != null) documentSet.setFromDate(documentSetDto.getFromDate());

            if (documentSetDto.getToDate() != null) documentSet.setToDate(documentSetDto.getToDate());

            if (documentSetDto.getType() != null) {

                String rowNumber = generateRowNumber(documentSetDto.getType(), documentSet.getFirstState().getCreatedBy().getBranch().getBranchCode());
                folderService.renameFolder("/okm:root/documentSets/" + documentSet.getRowsNumber() + documentSet.getSequence(), rowNumber + documentSet.getSequence());
                documentSet.setType(documentSetDto.getType());
                documentSet.setRowsNumber(rowNumber);
            }
            if (documentSetDto.getSequence() != null) {
                documentSet.setSequence(documentSetDto.getSequence());
                folderService.renameFolder("/okm:root/documentSets/" + documentSet.getRowsNumber() + documentSet.getSequence(), documentSet.getRowsNumber() + documentSetDto.getSequence());
            }
            if (!documentSetDto.getType().equals(DocumentSetTypeEnum.DAILY) && !documentSetDto.getType().equals(DocumentSetTypeEnum.CHAKAVAK)) {
                if (documentSetDto.getCustomerNumber() != null && !documentSetDto.getCustomerNumber().isEmpty())
                    documentSet.setCustomerNumber(documentSetDto.getCustomerNumber());
                if (documentSetDto.getFileNumber() != null && !documentSetDto.getFileNumber().isEmpty())
                    documentSet.setFileNumber(documentSetDto.getFileNumber());
                if (documentSetDto.getFileStatusId() != null)
                    documentSet.setFileStatus(fileStatusMapper.formId(documentSetDto.getFileStatusId()));

            }
            if (documentSetDto.getDescription() != null) documentSet.setDescription(documentSetDto.getDescription());
            documentSet = documentSetRepository.save(documentSet);
            documentSetDto = documentSetMapper.toDto(documentSet);
            log.debug("Request to update DocumentSet : {}", documentSetDto);
            return documentSetDto;
        } else throw forbidden("You cannot change this documentSet");
    }

    @Override
    @Transactional
    public DocumentSetDto findById(Long id) {
        log.debug("Request to get DocumentSet : {}", id);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(id));
        DocumentSet documentSet = documentSetRepository.findById(id).orElseThrow(() -> notFound("DocumentDet not found"));
        if (documentSet.getState().getProfileSeen().stream().noneMatch(profile -> profile.getId().equals(currentUser().getId()))) {
            documentSet.getState().getProfileSeen().add(currentUser());
            documentSetRepository.save(documentSet);
        }
        List<DocumentDto> documents = documentRepository.findByDocumentSetId(documentSet.getId()).stream().map(documentMapper::toDto).collect(Collectors.toList());
        DocumentSetDto documentSetDto = documentSetMapper.toDto(documentSet);
        documentSetDto.setDocuments(documents);
        return documentSetDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSetDto> findAll(Pageable pageable) {
        log.debug("Request to get all DocumentSets");
        return documentSetRepository.findAll(pageable).map(documentSetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSetDto> getDocumentSetByBranch(Pageable pageable) {
        log.debug("Request to get find DocumentSet by branch");
        Page<DocumentSetDto> page = null;
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        if (profile.getRole().equals(ADMIN))
            page = documentSetRepository.findAll(pageable).map(documentSetMapper::toDto);
        else if (profile.getRole().equals(BA)) {
            page = documentSetRepository.findAllByBranch_idIn(branchRepository.getAllByParentId(profile.getBranch().getId()), pageable).map(documentSetMapper::toDto);
        } else if (profile.getRole().equals(BU))
            page = documentSetRepository.findAllByBranch_idIn(Collections.singletonList(profile.getBranch().getId()), pageable).map(documentSetMapper::toDto);
        return page;
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DocumentSet : {}", id);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(id));
        DocumentSet documentSet = documentSetRepository.findById(id).orElseThrow(() -> notFound("documentSet not found"));
        List<Document> documents = documentRepository.findByDocumentSetId(documentSet.getId());
        if (!documents.isEmpty()) {
            String uuid = documents.stream().filter(document -> document.getFile() != null).findFirst().get().getFile().getUuid();
            String parentUuid = nodeBaseRepository.getParent(uuid);
            nodeBaseRepository.deleteByParentUuid(parentUuid);
            nodeBaseRepository.deleteByUuid(parentUuid);
            folderService.deleteFolder(parentUuid);
        }
        documentSetRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        log.debug("Request to delete DocumentSet : {}", ids);
        permissionService.checkPermission(ObjectName.DocumentSet, ids);
        List<DocumentSet> documentSets = documentSetRepository.findAllByIdIn(ids);
        if (documentSets != null && !documentSets.isEmpty()) {
            documentSets.forEach(documentSet -> {
                List<Document> documents = documentRepository.findByDocumentSetId(documentSet.getId());
                if (!documents.isEmpty() && documents.stream().findFirst().get().getFile() != null) {
                    String uuid = documents.stream().filter(document -> document.getFile() != null).findFirst().get().getFile().getUuid();
                    String parentUuid = nodeBaseRepository.getParent(uuid);
                    folderService.deleteFolder(parentUuid);
                    nodeBaseRepository.deleteByParentUuid(parentUuid);
                    nodeBaseRepository.deleteByUuid(parentUuid);
                }
            });
        }
        documentSetRepository.deleteAllById(ids);
    }

    @Override
    public Page<DocumentSetDto> getDocumentSetsByStatesAndBranchIds(List<DocumentSetStateEnum> states, Pageable pageable) {
        log.debug("Request to get DocumentSets by states : {}", states);
        Long currentUserId = currentUser().getId();
        List<Long> branchIds = branchRepository.getAssignBranches(currentUserId);
        RoleEnum role = profileRepository.getRole(currentUserId);
        if ((isNull(branchIds) || branchIds.isEmpty()) && (role.equals(DOA) || role.equals(ADMIN))) {
            return documentSetRepository.getDocumentSetsByStates(states, pageable).map(documentSetMapper::toDto);
        } else
            return documentSetRepository.getDocumentSetsByStatesAndBranchIds(states, branchIds, pageable).map(documentSetMapper::toDto);
    }

    @Override
    public Page<DocumentSetDto> getDocumentSetsByStatesAndBranchId(List<DocumentSetStateEnum> states, Long branchId, Pageable pageable) {
        log.debug("Request to get DocumentSets by states and branchId : {}", states, branchId);
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        Page<DocumentSetDto> page = null;
        if (profile.getRole().equals(ADMIN))
            page = documentSetRepository.findAllByState_NameIn(states, pageable).map(documentSetMapper::toDto);
        else if (profile.getRole().equals(BA))
            page = documentSetRepository.findAllByState_NameInAndBranch_idIn(states, branchRepository.getAllByParentId(profile.getBranch().getId()), pageable).map(documentSetMapper::toDto);
        else if (profile.getRole().equals(BU))
            page = documentSetRepository.findAllByState_NameInAndBranch_idIn(states, Collections.singletonList(branchId), pageable).map(documentSetMapper::toDto);
        return page;
    }

    @Override
    public DocumentSetDto branchConfirmDocumentSet(Long documentSetId) {

        log.debug("Request to confirm DocumentSet : {}", documentSetId);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetId));
        DocumentSet documentSet = documentSetRepository.findById(documentSetId).orElseThrow(() -> notFound("DocumentSet not found"));

        if (documentSet.getState().getName().equals(REGISTERED)) {
            complete(documentSet, confirm, null);
            return documentSetMapper.toDto(documentSetRepository.save(documentSet));

        } else {
            log.error("Branch confirm DocumentSet : " + documentSetId);
            throw conflict("DocumentSet should be in REGISTERED state");
        }
    }

    @Override
    public List<DocumentSetStateDto> getDocumentSetHistory(Long documentSetId) {
        log.debug("Request to get DocumentSet history: {}", documentSetId);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetId));
        return documentSetStateMapper.toDto(documentSetStateRepository.findAllByDocumentSet_Id(documentSetId));
    }

    @Override
    public DocumentSetDto setConflict(SetConflictRequestDto dto) {

        log.debug("Request to set conflict for DocumentSet : {}", dto);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(dto.getDocumentSetId()));

        DocumentSet documentSet = documentSetRepository.findById(dto.getDocumentSetId()).orElseThrow(() -> notFound("DocumentSet not found"));

        if (dto.getConflictReasons() == null || dto.getConflictReasons().isEmpty()) {
            throw badRequest("conflict Reasons must not be null");
        }
        if (documentSet.getState().getName().equals(BRANCH_CONFIRMED)) {
            DocumentSetConflictDto conflict = new DocumentSetConflictDto();
            conflict.setRegisterDescription(dto.getDescription());
            conflict.setConflictReasons(dto.getConflictReasons().stream().map(conflictReasonMapper::dtoFormId).collect(Collectors.toList()));
            documentSet.setConflicts(Collections.singletonList(documentSetConflictMapper.toEntity(conflict)));
            complete(documentSet, conflicting, null);

            return documentSetMapper.toDto(documentSetRepository.save(documentSet));

        } else {
            log.error("Set conflict : " + dto);
            throw conflict("DocumentSet should be in branch confirm state");
        }
    }

    @Override
    public List<DocumentSetDto> setAllConflict(SetAllConflictRequestDto dto) {

        log.debug("Request to set all conflict for DocumentSet : {}", dto);
        permissionService.checkPermission(ObjectName.DocumentSet, dto.getDocumentSetIds());
        if (dto.getConflictReasons() == null || dto.getConflictReasons().isEmpty()) {
            throw badRequest("conflict Reasons must not be null");
        }
        List<DocumentSet> documentSets = documentSetRepository.findAllByIdIn(dto.getDocumentSetIds());
        if (documentSets != null && !documentSets.isEmpty()) {
            documentSets.forEach(documentSet -> {
                if (documentSet.getState().getName().equals(BRANCH_CONFIRMED)) {
                    DocumentSetConflictDto conflict = new DocumentSetConflictDto();
                    conflict.setRegisterDescription(dto.getDescription());
                    conflict.setConflictReasons(dto.getConflictReasons().stream().map(conflictReasonMapper::dtoFormId).collect(Collectors.toList()));
                    documentSet.setConflicts(Collections.singletonList(documentSetConflictMapper.toEntity(conflict)));
                    complete(documentSet, conflicting, null);

                } else {
                    log.error("Set conflict : " + dto);
                    throw conflict("DocumentSet should be in branch confirm state");
                }
            });
            documentSetRepository.saveAll(documentSets);
            return documentSetMapper.toDto(documentSets);
        } else throw notFound("DocumentSet not found");
    }

    @Override
    public DocumentSetDto fixConflict(FixConflictRequestDto dto) {

        log.debug("Request to fix DocumentSet conflict : {}", dto);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(dto.getDocumentSetId()));
        DocumentSet documentSet = documentSetRepository.findById(dto.getDocumentSetId()).orElseThrow(() -> notFound("documentSet not found"));

        if (documentSet.getConflicts() != null && !documentSet.getConflicts().isEmpty()) {

            Optional<DocumentSetConflict> conflict = documentSet.getConflicts().stream().filter(c -> c.getResolver() == null).findFirst();

            conflict.ifPresent(c -> {

                if (c.getDocumentSet().getState().getName().equals(CONFLICTING)) {
                    c.setResolvingDate(Instant.now());
                    c.setResolveDescription(dto.getDescription());
                    c.setResolver(currentUser());

                    complete(c.getDocumentSet(), null, null);
                    documentSetRepository.save(documentSet);

                } else {
                    log.error("DocumentSet state is not conflicting : " + dto);
                    throw conflict("DocumentSet should be in conflicting state");
                }
            });
        } else {
            log.error("conflict not found : " + dto);
            throw notFound("The documentSet has no conflict");
        }
        return documentSetMapper.toDto(documentSet);
    }

    @Override
    public synchronized DocumentSetDto scanProcess(Long documentSetId, MultipartFile content) throws IOException {

        log.debug("Request to scan DocumentSet ", documentSetId);
        Instant uploadStart = Instant.now();
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetId));
        Tika tika = new Tika();
        String mimeType = tika.detect(content.getInputStream());
        if (!mimeType.equals("application/zip")) {
            throw badRequest("The file mimeType must be zip");
        }

        String extension = FilenameUtils.getExtension(content.getOriginalFilename());
        if (!extension.equals("zip")) {
            throw badRequest("The file format must be zip");
        }
        DocumentSet documentSet = documentSetRepository.findById(documentSetId).orElseThrow(() -> notFound("DocumentSet not found"));

        if (documentSet.getState().getName().equals(PRIMARY_CONFIRMED)) {

            Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
            Branch branch = profile.getBranch();
            Long branchCode = branch.getBranchCode();
            String branchName = branch.getBranchName();
            String folder = documentSet.getRowsNumber() + documentSet.getSequence();

            if (!folderService.isValidFolder(rootPath + documentSetPath)) folderService.createFolder(documentSetFolder);

            if (!folderService.isValidFolder(rootPath + documentSetPath + branchName + "-" + branchCode))
                folderService.createFolder(documentSetPath + branchName + "-" + branchCode);

            if (!folderService.isValidFolder(rootPath + documentSetPath + branchName + "-" + branchCode + "/" + folder))
                folderService.createFolder(documentSetPath + branchName + "-" + branchCode + "/" + folder);

            String fileNameWithOutExt = FilenameUtils.removeExtension(content.getOriginalFilename());
            Map<String, InputStream> files = UnzipFile.unzip(content.getInputStream());

            if (!files.isEmpty()) {
                int index = 1;
                for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                    try {
                        com.openkm.sdk4j.bean.Document file = new com.openkm.sdk4j.bean.Document();
                        String fileName = documentSet.getRowsNumber() + documentSet.getSequence() + "_" + (index++) + FileUtils.getFormat(entry.getKey());
                        file.setPath(rootPath + documentSetPath + branchName + "-" + branchCode + "/" + folder + "/" + FileUtils.getInstant() + FileUtils.getFormat(entry.getKey()));

                        file = nodeDocumentService.createDocument(file, entry.getValue());

                        DocumentDto documentDto = new DocumentDto();
                        DocumentStateDto documentState = new DocumentStateDto(DocumentStateEnum.NOT_CHECKED, profile.getUser().getId());

                        Document document = documentMapper.toEntity(documentDto);

                        document.setCreatedBy(documentSet.getCreatedBy());
                        document.setName(fileName);
                        document.setMaintenanceCode(fileNameWithOutExt);
                        document.setDocumentSet(documentSet);
                        document.setState(documentStateMapper.toEntity(documentState));
                        document.setStates(Collections.singletonList(document.getState()));
                        document = documentRepository.save(document);

                        documentRepository.setFile(document.getId(), file.getUuid());

                    } catch (Exception e) {
                        List<Document> documents = documentRepository.findByDocumentSetId(documentSet.getId());
                        String uuid = documents.stream().filter(document -> document.getFile() != null).findFirst().get().getFile().getUuid();
                        String parentUuid = nodeBaseRepository.getParent(uuid);
                        nodeDocumentService.deleteFolder(parentUuid);
                        nodeBaseRepository.deleteByParentUuid(parentUuid);
                        nodeBaseRepository.deleteByUuid(parentUuid);
                        documentRepository.deleteAllByDocumentSet_id(documentSetId);
                    }
                }
                complete(documentSet, null, null);
                documentSet.setFileSize(content.getSize());
                documentSet.setUploadStart(uploadStart);
                documentSet.setUploadEnd(Instant.now());
                documentSetRepository.save(documentSet);
                return documentSetMapper.toDto(documentSet);
            } else {
                throw forbidden("file can not be empty");
            }
        } else {
            log.error("Scan DocumentSet Process : " + documentSetId);
            throw conflict("DocumentSet should be in primary confirmed state");
        }
    }

    @Override
    public DocumentSetDto rescan(Long documentSetId) {

        log.debug("Request to rescan Documents ", documentSetId);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetId));
        DocumentSet documentSet = documentSetRepository.findById(documentSetId).orElseThrow(() -> notFound("Documents not found"));
        List<Document> documents = documentRepository.findByDocumentSetId(documentSet.getId());
        if (documentSet.getState().getName().equals(SCANNED) || documentSet.getState().getName().equals(PROCESSED) && !documents.isEmpty()) {


            String uuid = documents.stream().filter(document -> document.getFile() != null).findFirst().get().getFile().getUuid();
            String parentUuid = nodeBaseRepository.getParent(uuid);
            nodeDocumentService.deleteFolder(parentUuid);
            nodeBaseRepository.deleteByParentUuid(parentUuid);
            nodeBaseRepository.deleteByUuid(parentUuid);

            documentRepository.deleteAllByDocumentSet_id(documentSetId);
            DocumentSetState state = new DocumentSetState(PRIMARY_CONFIRMED);
            state.setLastState(documentSet.getState());
            documentSet.setState(state);
            documentSet.addStates(Collections.singletonList(state));
            documentSet = documentSetRepository.save(documentSet);

            return documentSetMapper.toDto(documentSetRepository.save(documentSet));

        } else {
            log.error("Rescan DocumentSet Process : " + documentSetId);
            throw conflict("DocumentSet should be in scanned state and have zip file");
        }
    }

    @Async
    @Scheduled(fixedDelay = 60000)
    public void ocrCompleted() {

        List<DocumentSet> documentSets = documentSetRepository.findByState_Name(SCANNED);

        if (documentSets != null && !documentSets.isEmpty()) {
            documentSets.forEach(documentSet -> {
                int processed = documentRepository.countAllByProcessState(documentSet.getId());
                if (processed > 0) {
                    complete(documentSet, null, null);
                    documentSet.setOcr(true);
                    documentSet.setOcrFinishedTime(Instant.now());
                    documentSet.setLastModifiedBy(null);
                    log.info("documentSet ({}) " + documentSet.getId() + " has been processed.");
                }
            });
            documentSetRepository.saveAll(documentSets);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void completeDocumentSet() {
        List<DocumentSet> documentSets = documentSetRepository.findByState_Name(PROCESSED);
        if (documentSets != null && !documentSets.isEmpty()) {
            documentSets.forEach(documentSet -> {
                List<Document> documents = documentRepository.findByDocumentSetId(documentSet.getId());
                if (documents != null && !documents.isEmpty()) {
                    if ((Arrays.asList(STAGNANT, CONFLICTED_STAGNANT)).containsAll(documents.stream().map(document -> document.getState().getName()).collect(Collectors.toList()))) {
                        complete(documentSet, null, null);
                        log.info("documentSet ({}) " + documentSet.getId() + " has been completed.");
                    }
                }
            });
            documentSetRepository.saveAll(documentSets);
        }
    }

    @Override
    public Page<DocumentSetDto> advanceSearch(DocumentSetTypeEnum type, List<DocumentSetStateEnum> state, String fromDate, String toDate, String registerFromDate, String registerToDate, String sentFromDate, String sentToDate, Long registrarId, Long confirmerId, Long scannerId, String rowNumber, List<Long> branchIds, String reason, String customerNumber, String fileNumber, Long fileStatusId, Long fileTypeId, Pageable pageable) {

        log.debug("Request to search documentSet : {}");

        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        RoleEnum role = profileRepository.getRole(profile.getId());
        if (role.equals(ADMIN) || role.equals(DOA) || role.equals(RU))
            return documentSetRepository.advanceSearchAdminAndDoa(type, state, fromDate != null ? DateUtils.toDate(fromDate) : null, toDate != null ? DateUtils.toDate(toDate) : null, registerFromDate != null ? DateUtils.toDate(registerFromDate) : null, registerToDate != null ? DateUtils.toDate(registerToDate) : null, registrarId, sentFromDate != null ? DateUtils.toDate(sentFromDate) : null, sentToDate != null ? DateUtils.toDate(sentToDate) : null, confirmerId, scannerId, rowNumber, branchIds, reason, customerNumber, fileNumber, fileStatusId, fileTypeId, pageable).map(documentSetMapper::toDto);


        if (role.equals(BU)) {
            branchIds = Collections.singletonList(profile.getBranch().getId());
        } else if (role.equals(BA)) branchIds = branchRepository.getAllByParentId(profile.getBranch().getId());
        else if (role.equals(DOPU) || role.equals(DOEU)) {
            if (branchIds == null || branchIds.isEmpty())
                branchIds = profile.getAssignedBranches().stream().map(Branch::getId).collect(Collectors.toList());
        }

        return documentSetRepository.advanceSearch(type, state, fromDate != null ? DateUtils.toDate(fromDate) : null, toDate != null ? DateUtils.toDate(toDate) : null, registerFromDate != null ? DateUtils.toDate(registerFromDate) : null, registerToDate != null ? DateUtils.toDate(registerToDate) : null, registrarId, sentFromDate != null ? DateUtils.toDate(sentFromDate) : null, sentToDate != null ? DateUtils.toDate(sentToDate) : null, confirmerId, scannerId, rowNumber, branchIds, reason, customerNumber, fileNumber, fileStatusId, fileTypeId, pageable).map(documentSetMapper::toDto);
    }

//    @Override
//    public List<OcrDocumentReportDto> report(String registerFromDate, String registerToDate) {
//        return documentSetRepository.report(registerFromDate != null ? DateUtils.toDate(registerFromDate) : null,
//                registerToDate != null ? DateUtils.toDate(registerToDate) : null);
//    }

    @Override
    public DocumentSetDto uploadFile(Long documentSetId, MultipartFile file) throws IOException {
        log.debug("Request to upload file to documentSet : {}", documentSetId);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetId));
        if (file == null || file.isEmpty()) throw badRequest("files must not be null");
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        if (!mimeType.equals("image/tif") && !mimeType.equals("application/pdf") && !mimeType.equals("image/tiff")) {
            throw Responses.badRequest("The file mimeType must be tif or pdf");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!extension.equals("tif") && !extension.equals("pdf") && !extension.equals("tiff")) {
            throw Responses.badRequest("The file format must be tif or pdf");
        }
        DocumentSet documentSet = documentSetRepository.findById(documentSetId).orElseThrow(() -> Responses.notFound("request not found"));


        if (documentSet.getState().getName().equals(PROCESSED)) {
            Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
            Branch branch = profile.getBranch();
            Long branchCode = branch.getBranchCode();
            String branchName = branch.getBranchName();
            String folder = documentSet.getRowsNumber() + documentSet.getSequence();

            if (!folderService.isValidFolder(rootPath + documentSetPath)) folderService.createFolder(documentSetFolder);

            if (!folderService.isValidFolder(rootPath + documentSetPath + branchName + "-" + branchCode))
                folderService.createFolder(documentSetPath + branchName + "-" + branchCode);

            if (!folderService.isValidFolder(rootPath + documentSetPath + branchName + "-" + branchCode + "/" + folder))
                folderService.createFolder(documentSetPath + branchName + "-" + branchCode + "/" + folder);

            com.openkm.sdk4j.bean.Document nodeDocument = new com.openkm.sdk4j.bean.Document();
            String fileName = documentSet.getRowsNumber() + documentSet.getSequence() + "_" + (documentSet.getDocumentSize() + 1) + FileUtils.getFormat(file.getOriginalFilename());
            nodeDocument.setPath(rootPath + documentSetPath + branchName + "-" + branchCode + "/" + folder + "/" + FileUtils.getInstant() + FileUtils.getFormat(file.getOriginalFilename()));

            nodeDocument = nodeDocumentService.createDocument(nodeDocument, file.getInputStream());

            DocumentDto documentDto = new DocumentDto();
            DocumentStateDto documentState = new DocumentStateDto(DocumentStateEnum.NOT_CHECKED, profile.getUser().getId());

            Document document = documentMapper.toEntity(documentDto);

            String maintenanceCode = documentRepository.getMaintenanceCode(documentSet.getId());
            document.setCreatedBy(documentSet.getCreatedBy());
            document.setName(fileName);
            document.setMaintenanceCode(maintenanceCode);
            document.setDocumentSet(documentSet);
            document.setState(documentStateMapper.toEntity(documentState));
            document.setStates(Collections.singletonList(document.getState()));
            document = documentRepository.save(document);
            documentRepository.setFile(document.getId(), nodeDocument.getUuid());

            documentSetRepository.save(documentSet);
            return documentSetMapper.toDto(documentSet);

        } else {
            log.error("upload file to  DocumentSet Process : " + documentSetId);
            throw conflict("DocumentSet should be in scanned state");
        }
    }

    @Override
    public DocumentSetDto complete(Long documentSetId, WorkflowOperation operation, String description) {
        log.debug("Request to complete DocumentSet : {}", documentSetId, operation, description);
        permissionService.checkPermission(ObjectName.DocumentSet, Collections.singletonList(documentSetId));
        RoleEnum role = profileRepository.getRole(currentUserId());
        try {
            DocumentSet documentSet = documentSetRepository.findById(documentSetId).orElseThrow(() -> notFound("DocumentSet not found"));

            DocumentSetStateEnum nextStep = nextStep(documentSet.getState().getName(), operation, role);
            DocumentSetState state = new DocumentSetState(nextStep, documentSet.getState(), description);
            documentSet.setState(state);
            documentSet.addStates(Collections.singletonList(state));
            return documentSetMapper.toDto(documentSetRepository.save(documentSet));

        } catch (Exception e) {
            log.error("complete DocumentSet : " + documentSetId + " " + e.getMessage());
            throw forbidden("DocumentSet not completed");
        }
    }

    @Override
    public List<DocumentSetDto> complete(List<Long> ids, WorkflowOperation operation, String description) {
        log.debug("Request to complete DocumentSets : {}", ids, operation, description);
        permissionService.checkPermission(ObjectName.DocumentSet, ids);
        RoleEnum role = profileRepository.getRole(currentUserId());
        try {
            List<DocumentSet> documentSets = documentSetRepository.findAllByIdIn(ids);
            documentSets.forEach(documentSet -> {
                DocumentSetStateEnum nextStep = nextStep(documentSet.getState().getName(), operation, role);

                DocumentSetState state = new DocumentSetState(nextStep, documentSet.getState(), description);
                documentSet.setState(state);
                documentSet.addStates(Collections.singletonList(state));
            });
            documentSetRepository.saveAll(documentSets);
            return documentSetMapper.toDto(documentSets);

        } catch (Exception e) {
            log.error("Complete DocumentSets : " + ids + " " + e.getMessage());
            throw forbidden("DocumentSets not completed");
        }
    }

    public void complete(DocumentSet documentSet, WorkflowOperation operation, String description) {
        log.debug("Request to complete documentSet : {}", documentSet, operation, description);
        try {
            DocumentSetStateEnum nextStep = nextStep(documentSet.getState().getName(), operation, null);
            DocumentSetState state = new DocumentSetState(nextStep, documentSet.getState(), description);
            documentSet.setState(state);
            documentSet.addStates(Collections.singletonList(state));
        } catch (Exception e) {
            log.error("complete DocumentSet : " + documentSet.getId() + " " + e.getMessage());
            throw forbidden("DocumentSet not completed");
        }
    }

    private String generateRowNumber(DocumentSetTypeEnum documentSetTypeEnum, Long branchCode) {
        String year = JalaliCalendar.getJalaliYear(Date.from(Instant.now()));
        return documentSetTypeEnum.getCode() + year + branchCode;
    }

}
