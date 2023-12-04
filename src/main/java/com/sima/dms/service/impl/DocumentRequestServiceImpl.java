package com.sima.dms.service.impl;

import com.sima.dms.constants.OpenKM;
import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.SentDocumentRequestDto;
import com.sima.dms.domain.dto.document.DocumentRequestDto;
import com.sima.dms.domain.dto.document.DocumentRequestStateDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.document.DocumentRequest;
import com.sima.dms.domain.entity.document.DocumentRequestState;
import com.sima.dms.domain.enums.*;
import com.sima.dms.repository.*;
import com.sima.dms.service.DocumentRequestService;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.NodeDocumentService;
import com.sima.dms.service.PermissionService;
import com.sima.dms.service.mapper.DocumentRequestMapper;
import com.sima.dms.service.mapper.DocumentRequestStateMapper;
import com.sima.dms.tools.FileUtils;
import com.sima.dms.utils.DateUtils;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.enums.RequestTypeEnum.*;
import static com.sima.dms.utils.Responses.badRequest;


@Service
@AllArgsConstructor
public class DocumentRequestServiceImpl implements DocumentRequestService {

    private final FolderService folderService;
    private final NodeDocumentService nodeDocumentService;
    private final PermissionService permissionService;
    private final DocumentRequestMapper documentRequestMapper;
    private final DocumentRequestStateMapper documentRequestStateMapper;

    private final ProfileRepository profileRepository;
    private final BranchRepository branchRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final DocumentRequestStateRepository documentRequestStateRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final RequestReasonValidationRepository requestReasonValidationRepository;

    private final Logger log = LoggerFactory.getLogger(DocumentRequestServiceImpl.class);

    @Transactional
    @Override
    public DocumentRequestDto save(DocumentRequestDto documentRequestDto, MultipartFile file) throws IOException {

        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        if (documentRequestDto.getRequestType().equals(GROUP_DOCUMENT_IMAGE) && (profile.getRole().equals(RoleEnum.BU) || profile.getRole().equals(RoleEnum.BA)))
            throw Responses.unauthorized("you have not permission to this operation.");

        List<FieldNameEnum> fieldNames = requestReasonValidationRepository.getFieldNameByRequestReasonId(documentRequestDto.getDocumentRequestReasonId());
        fieldNames.forEach(fieldName -> {
            try {
                if (!fieldName.equals(FieldNameEnum.file)) {
                    Field field = documentRequestDto.getClass().getDeclaredField(fieldName.name());
                    field.setAccessible(true);
                    if (field.get(documentRequestDto) == null) {
                        throw Responses.forbidden(fieldName.name() + " must not be null");
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("check notNull Fields  : " + fieldNames + " " + e.getMessage());
                throw Responses.forbidden("documentRequestDto not check nullField");
            }
        });

        if (fieldNames.contains(FieldNameEnum.file) && file == null)
            throw Responses.forbidden("file must not be null");

        this.notNullFieldsValidation(documentRequestDto);

        if (documentRequestDto.getDocumentDateFrom() != null && documentRequestDto.getDocumentDateTo() != null && documentRequestDto.getDocumentDateFrom().isAfter(documentRequestDto.getDocumentDateTo()))
            throw badRequest("the documentDateTo must not be earlier than the documentDateFrom");

        if (documentRequestDto.getFileDateFrom() != null && documentRequestDto.getFileDateTo() != null && documentRequestDto.getFileDateFrom().isAfter(documentRequestDto.getFileDateTo()))
            throw badRequest("the fileDateTo must not be earlier than the fileDateFrom");

        if (documentRequestDto.getCheckReceiptDateFrom() != null && documentRequestDto.getCheckReceiptDateTo() != null && documentRequestDto.getCheckReceiptDateFrom().isAfter(documentRequestDto.getCheckReceiptDateTo()))
            throw badRequest("the checkReceiptDateTo must not be earlier than the checkReceiptDateFrom");

        DocumentRequest documentRequest = documentRequestMapper.toEntity(documentRequestDto);
        documentRequest.setRequestBranch(profile.getBranch());
        documentRequest.setLastState(new DocumentRequestState(documentRequest, DocumentRequestStateEnum.REGISTERED, documentRequest.getRequestDescription()));
        documentRequest = documentRequestRepository.save(documentRequest);

        com.openkm.sdk4j.bean.Document document = new com.openkm.sdk4j.bean.Document();
        if (file != null && !file.isEmpty()) {
            String fileFormat = FileNameUtils.getExtension(file.getOriginalFilename());
            if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.documentRequestFolderPath))
                folderService.createFolder(OpenKM.documentRequestFolder);
            document.setPath(OpenKM.rootPath + OpenKM.documentRequestFolderPath + FileUtils.uniqueName(documentRequestDto.getDocumentNumber()) + '.' + fileFormat);
            document = nodeDocumentService.createDocument(document, file.getInputStream());
        }
        DocumentRequestDto dto = documentRequestMapper.toDto(documentRequest);
        if (document.getUuid() != null) {
            documentRequestRepository.setBranchFile(documentRequest.getId(), document.getUuid());
            dto.setBranchFileUuid(document.getUuid());
        }
        log.debug("Request to save Document", dto);
        return dto;
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Document according by id : {}", id);
        permissionService.checkPermission(ObjectName.DocumentRequest, Collections.singletonList(id));
        documentRequestRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DocumentRequestDto findOne(Long id) {
        log.debug("Request to get specific  Document request by id : {}", id);
        permissionService.checkPermission(ObjectName.DocumentRequest, Collections.singletonList(id));
        DocumentRequest documentRequest = documentRequestRepository.findById(id)
                .orElseThrow(() -> Responses.notFound("documentRequest not found"));
        if (documentRequest.getLastState().getProfileSeen().stream().noneMatch(profile -> profile.getId().equals(currentUser().getId()))) {
            documentRequest.getLastState().getProfileSeen().add(currentUser());
            documentRequestRepository.save(documentRequest);
        }
        DocumentRequestDto documentRequestDto = documentRequestMapper.toDto(documentRequest);
        List<DocumentRequestStateDto> documentRequestStates = documentRequestStateRepository.findAllByDocumentRequestIdOrderByRegisterDate(id).stream().map(documentRequestStateMapper::toDto).collect(Collectors.toList());
        documentRequestDto.setStates(documentRequestStates);
        return documentRequestDto;
    }

    @Override
    public Page<DocumentRequestDto> findByState(List<DocumentRequestStateEnum> states, Pageable pageable) {
        log.debug("Request to find document requests by state : {}", states);
        return documentRequestRepository.findAllByStateNotIn(states, pageable).map(documentRequestMapper::toDto);
    }

    @Override
    public Page<DocumentRequestDto> findAll(Pageable pageable) {
        log.debug("Request to get all  Document by pagination");
        return documentRequestRepository.findAll(pageable).map(documentRequestMapper::toDto);
    }

    @Override
    @Transactional
    public DocumentRequestDto uploadFile(Long requestId, String description, List<MultipartFile> files) {

        log.debug("Request to upload file to request : {}", requestId);
        permissionService.checkPermission(ObjectName.DocumentRequest, Collections.singletonList(requestId));
        DocumentRequest documentRequest = documentRequestRepository.findById(requestId)
                .orElseThrow(() -> Responses.notFound("request not found"));
        if (files == null || files.isEmpty())
            throw badRequest("files must not be null");
        Tika tika = new Tika();

        if (documentRequest.getLastState().getState().equals(DocumentRequestStateEnum.DOCUMENT_OFFICE_CONFIRMED)) {
            if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.documentRequestFolderPath))
                folderService.createFolder(OpenKM.documentRequestFolder);
            files.forEach(file -> {
                try {
                    String fileFormat = FileNameUtils.getExtension(file.getOriginalFilename());
                    String mimeType = tika.detect(file.getInputStream());

                    if (documentRequest.getRequestType().equals(GROUP_DOCUMENT_IMAGE) && files.size() != 1) {
                        throw Responses.badRequest("File format must be zip");
                    } else if (documentRequest.getRequestType().equals(GROUP_DOCUMENT_IMAGE) && files.size() == 1 && (!fileFormat.equals("zip") || !mimeType.equals("application/zip")))
                        throw Responses.badRequest("File format must be zip");
                    com.openkm.sdk4j.bean.Document document = new com.openkm.sdk4j.bean.Document();
                    document.setPath(OpenKM.rootPath + OpenKM.documentRequestFolderPath + FileUtils.uniqueName(documentRequest.getDocumentNumber()) + '.' + fileFormat);

                    document = nodeDocumentService.createDocument(document, file.getInputStream());
                    documentRequestRepository.setDocumentOfficeFile(documentRequest.getId(), document.getUuid());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            documentRequest.setLastState(new DocumentRequestState(documentRequest, DocumentRequestStateEnum.nextState(documentRequest.getLastState().getState(), null), description));
            documentRequestRepository.save(documentRequest);
            return documentRequestMapper.toDto(documentRequest);
        } else {
            log.error("upload file process : " + requestId);
            throw Responses.conflict("request should be in document office confirmed state");
        }
    }

    @Override
    public DocumentRequestDto receiveDocument(Long id) {
        log.debug("Request to receive document request : {}", id);
        permissionService.checkPermission(ObjectName.DocumentRequest, Collections.singletonList(id));
        DocumentRequest documentRequest = documentRequestRepository.findById(id)
                .orElseThrow(() -> Responses.notFound("request not found"));
        DocumentRequestStateEnum state = documentRequest.getLastState().getState();
        if (documentRequest.getExpiryDate() != null && documentRequest.getExpiryDate().isBefore(Instant.now()))
            throw Responses.forbidden("expire date is not valid");

        if (state.equals(DocumentRequestStateEnum.SENT_DOCUMENT_REQUESTED)) {
            documentRequest.setLastState(new DocumentRequestState(documentRequest, DocumentRequestStateEnum.nextState(state, null)));
            documentRequest = documentRequestRepository.save(documentRequest);
            return documentRequestMapper.toDto(documentRequest);
        } else if (state.equals(DocumentRequestStateEnum.RECEIVE_DOCUMENT_REQUESTED))
            return documentRequestMapper.toDto(documentRequest);
        else throw Responses.conflict("request should be in SENT or RECEIVE state");
    }

    @Override
    public Page<DocumentRequestDto> getBranchRequests(Pageable pageable) {
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        Long branchId = profile.getBranch().getId();
        Page<DocumentRequestDto> documentRequestDtos = null;
        if (profile.getRole().equals(RoleEnum.ADMIN))
            documentRequestDtos = documentRequestRepository.getAllBranchRequests(pageable).map(documentRequestMapper::toDto);
        else if (profile.getRole().equals(RoleEnum.BA))
            documentRequestDtos = documentRequestRepository.getBranchRequests(branchRepository.getAllByParentId(profile.getBranch().getId()), pageable).map(documentRequestMapper::toDto);
        else if (profile.getRole().equals(RoleEnum.BU))
            documentRequestDtos = documentRequestRepository.getBranchRequests(Collections.singletonList(branchId), pageable).map(documentRequestMapper::toDto);
        return documentRequestDtos;
    }

    @Override
    public List<DocumentRequestDto> complete(List<Long> documentRequestIds, WorkflowOperationState operation, String description) {
        log.debug("Request to complete documentRequest : {}", documentRequestIds, operation);
        permissionService.checkPermission(ObjectName.DocumentRequest, documentRequestIds);
        try {
            if (operation.equals(WorkflowOperationState.reject) && (description == null || description.isEmpty()))
                throw Responses.forbidden("description can not be null");
            List<DocumentRequest> documentRequests = documentRequestRepository.findAllByIdIn(documentRequestIds);
            documentRequests.forEach(documentRequest -> {
                DocumentRequestStateEnum state = DocumentRequestStateEnum.nextState(documentRequest.getLastState().getState(), operation);
                documentRequest.setLastState(new DocumentRequestState(documentRequest, state, description));
                documentRequestRepository.save(documentRequest);
            });
            return documentRequestMapper.toDto(documentRequests);
        } catch (Exception e) {
            log.error("complete documentRequest : " + documentRequestIds + " " + e.getMessage());
            throw Responses.forbidden("documentRequest not completed");
        }
    }

    @Override
    public DocumentRequestDto send(SentDocumentRequestDto sentDocumentRequestDto) {
        log.debug("Request to send documentRequest ", sentDocumentRequestDto);
        permissionService.checkPermission(ObjectName.DocumentRequest, Collections.singletonList(sentDocumentRequestDto.getId()));
        if (sentDocumentRequestDto.getExpiryDate() == null || (sentDocumentRequestDto.getExpiryDate() != null && sentDocumentRequestDto.getExpiryDate().isBefore(Instant.now())))
            throw Responses.badRequest("expire date is not valid");

        DocumentRequest documentRequest = documentRequestRepository.findById(sentDocumentRequestDto.getId())
                .orElseThrow(() -> Responses.notFound("documentRequest not found"));

        if (documentRequest.getOfficeFiles() != null && documentRequest.getLastState().getState().equals(DocumentRequestStateEnum.UPLOAD_FILE_OFFICE)) {
            documentRequest.setLastState(new DocumentRequestState(documentRequest, DocumentRequestStateEnum.nextState(documentRequest.getLastState().getState(), null), sentDocumentRequestDto.getReceiveDescription()));
            documentRequest.setSentDate(Instant.now());
            documentRequest.setExpiryDate(sentDocumentRequestDto.getExpiryDate());
            documentRequest.setReceiveDescription(sentDocumentRequestDto.getReceiveDescription());
            documentRequest = documentRequestRepository.save(documentRequest);
            return documentRequestMapper.toDto(documentRequest);
        } else {
            throw Responses.badRequest("documentRequest state is not valid");
        }
    }

    public DocumentRequestDto updateExpiryDate(SentDocumentRequestDto updateExpiryDateDto) {
        log.debug("Request to update DocumentRequest expiry date {}", updateExpiryDateDto.getId(), updateExpiryDateDto.getExpiryDate());
        permissionService.checkPermission(ObjectName.DocumentRequest, Collections.singletonList(updateExpiryDateDto.getId()));
        if (updateExpiryDateDto.getExpiryDate() == null || (updateExpiryDateDto.getExpiryDate() != null && updateExpiryDateDto.getExpiryDate().isBefore(Instant.now())))
            throw Responses.badRequest("expire date is not valid");

        DocumentRequest documentRequest = documentRequestRepository.findById(updateExpiryDateDto.getId())
                .orElseThrow(() -> Responses.notFound("documentRequest not found"));
        if (documentRequest.getLastState().getState().equals(DocumentRequestStateEnum.SENT_DOCUMENT_REQUESTED) || documentRequest.getLastState().getState().equals(DocumentRequestStateEnum.RECEIVE_DOCUMENT_REQUESTED)) {
            documentRequest.setExpiryDate(updateExpiryDateDto.getExpiryDate());
            documentRequest = documentRequestRepository.save(documentRequest);
            return documentRequestMapper.toDto(documentRequest);
        } else {
            throw Responses.badRequest("documentRequest state is not valid");
        }
    }

    @Override
    public Page<DocumentRequestDto> branchRequestSearch(String registerDateFrom, String registerDateTo, String documentNumber, String documentDateFrom,
                                                        String documentDateTo, String customerNumber, DocumentRequestTypeEnum documentType, List<DocumentRequestStateEnum> states,
                                                        Long creatorId, Long confirmerId, List<Long> documentBranchIds,
                                                        List<Long> requestBranchIds, String sentDateFrom, String sentDateTo, Pageable pageable) {
        log.debug("Request to search documentRequest : {}");
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        RoleEnum role = profileRepository.getRole(profile.getId());
        if (states.isEmpty())
            states.addAll(Arrays.asList(DocumentRequestStateEnum.REGISTERED, DocumentRequestStateEnum.BRANCH_CONFIRMED, DocumentRequestStateEnum.DOCUMENT_OFFICE_CONFIRMED, DocumentRequestStateEnum.DOCUMENT_OFFICE_REJECTED, DocumentRequestStateEnum.UPLOAD_FILE_OFFICE, DocumentRequestStateEnum.SENT_DOCUMENT_REQUESTED, DocumentRequestStateEnum.RECEIVE_DOCUMENT_REQUESTED, DocumentRequestStateEnum.EXPIRED_REQUEST));
        if (role.equals(RoleEnum.BU)) {
            requestBranchIds = Collections.singletonList(profile.getBranch().getId());
        } else if (role.equals(RoleEnum.BA)) {
            requestBranchIds = branchRepository.getAllByParentId(profile.getBranch().getId());
        }
        return documentRequestRepository.requestSearch(
                registerDateFrom != null ? DateUtils.toDate(registerDateFrom) : null,
                registerDateTo != null ? DateUtils.toDate(registerDateTo) : null,
                documentNumber,
                documentDateFrom != null ? DateUtils.toDate(documentDateFrom) : null,
                documentDateTo != null ? DateUtils.toDate(documentDateTo) : null,
                customerNumber,
                documentType,
                states,
                creatorId,
                confirmerId,
                documentBranchIds,
                requestBranchIds,
                sentDateFrom != null ? DateUtils.toDate(sentDateFrom) : null,
                sentDateTo != null ? DateUtils.toDate(sentDateTo) : null,
                pageable
        ).map(documentRequestMapper::toDto);
    }

    @Override
    public Page<DocumentRequestDto> documentOfficeSearch(String registerDateFrom, String registerDateTo, String documentNumber, String documentDateFrom,
                                                         String documentDateTo, String customerNumber, DocumentRequestTypeEnum documentType, List<DocumentRequestStateEnum> states,
                                                         Long creatorId, Long confirmerId, List<Long> documentBranchIds, List<Long> requestBranchIds, String sentDateFrom,
                                                         String sentDateTo, Pageable pageable) {
        log.debug("Request to search documentRequest : {}");
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> Responses.notFound("profile not found"));
        RoleEnum role = profileRepository.getRole(profile.getId());
        if (states.isEmpty())
            states.addAll(Arrays.asList(DocumentRequestStateEnum.BRANCH_CONFIRMED, DocumentRequestStateEnum.DOCUMENT_OFFICE_CONFIRMED, DocumentRequestStateEnum.DOCUMENT_OFFICE_REJECTED, DocumentRequestStateEnum.UPLOAD_FILE_OFFICE, DocumentRequestStateEnum.SENT_DOCUMENT_REQUESTED, DocumentRequestStateEnum.RECEIVE_DOCUMENT_REQUESTED, DocumentRequestStateEnum.EXPIRED_REQUEST));
        else if (states.contains(DocumentRequestStateEnum.REGISTERED) || states.contains(DocumentRequestStateEnum.BRANCH_REJECTED))
            throw Responses.unauthorized("you have not permission to this operation.");
        if (role.equals(RoleEnum.DOPU) || role.equals(RoleEnum.DOEU)) {
            if (requestBranchIds == null || requestBranchIds.isEmpty())
                requestBranchIds = profile.getAssignedBranches().stream().map(Branch::getId).collect(Collectors.toList());

            return documentRequestRepository.requestSearchByDou(
                    registerDateFrom != null ? DateUtils.toDate(registerDateFrom) : null,
                    registerDateTo != null ? DateUtils.toDate(registerDateTo) : null,
                    documentNumber,
                    documentDateFrom != null ? DateUtils.toDate(documentDateFrom) : null,
                    documentDateTo != null ? DateUtils.toDate(documentDateTo) : null,
                    customerNumber,
                    documentType,
                    states,
                    creatorId,
                    confirmerId,
                    documentBranchIds,
                    requestBranchIds,
                    sentDateFrom != null ? DateUtils.toDate(sentDateFrom) : null,
                    sentDateTo != null ? DateUtils.toDate(sentDateTo) : null,
                    pageable
            ).map(documentRequestMapper::toDto);
        } else {
            return documentRequestRepository.requestSearch(
                    registerDateFrom != null ? DateUtils.toDate(registerDateFrom) : null,
                    registerDateTo != null ? DateUtils.toDate(registerDateTo) : null,
                    documentNumber,
                    documentDateFrom != null ? DateUtils.toDate(documentDateFrom) : null,
                    documentDateTo != null ? DateUtils.toDate(documentDateTo) : null,
                    customerNumber,
                    documentType,
                    states,
                    creatorId,
                    confirmerId,
                    documentBranchIds,
                    requestBranchIds,
                    sentDateFrom != null ? DateUtils.toDate(sentDateFrom) : null,
                    sentDateTo != null ? DateUtils.toDate(sentDateTo) : null,
                    pageable
            ).map(documentRequestMapper::toDto);
        }

    }

    @Override
    public void updateDocument(Long requestId, String uuid, MultipartFile file) throws IOException {
        log.debug("Request to update document : {}", uuid, file);
        NodeDocumentDto nodeDocument = nodeDocumentService.findByUuid(uuid);
        DocumentRequest documentRequest = documentRequestRepository.findById(requestId)
                .orElseThrow(() -> Responses.notFound("documentRequest not found"));

        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        String fileFormat = FileNameUtils.getExtension(file.getOriginalFilename());
        if (documentRequest.getRequestType().equals(GROUP_DOCUMENT_IMAGE) && (!fileFormat.equals("zip") || !mimeType.equals("application/zip")))
            throw Responses.badRequest("File format must be zip");

        if (mimeType.equals(nodeDocument.getMimeType())) {
            nodeDocumentService.updateDocument(uuid, file.getInputStream());
        } else {
            if (!folderService.isValidFolder(OpenKM.rootPath + OpenKM.documentRequestFolderPath))
                folderService.createFolder(OpenKM.documentRequestFolder);

            com.openkm.sdk4j.bean.Document document = new com.openkm.sdk4j.bean.Document();
            document.setPath(OpenKM.rootPath + OpenKM.documentRequestFolderPath + FileNameUtils.getBaseName(nodeDocument.getName()) + '.' + fileFormat);

            document = nodeDocumentService.createDocument(document, file.getInputStream());
            documentRequestRepository.updateUuid(nodeDocument.getUuid(), document.getUuid());
            nodeDocumentService.deleteDocument(nodeDocument.getUuid());
            nodeBaseRepository.deleteByUuid(nodeDocument.getUuid());
        }
    }

    private void notNullFieldsValidation(DocumentRequestDto documentRequestDto) {

        if (documentRequestDto.getRequestDescription() == null || documentRequestDto.getRequestDescription().isEmpty()) {
            throw Responses.forbidden("request description must not be null");
        }

        if (documentRequestDto.getDocumentType().equals(DocumentRequestTypeEnum.DAILY) && (documentRequestDto.getRequestType().equals(DOCUMENT_IMAGE) || documentRequestDto.getRequestType().equals(DOCUMENT_ORIGINAL))) {
            if (documentRequestDto.getDocumentNumber() == null || documentRequestDto.getDocumentNumber().isEmpty())
                throw Responses.forbidden("DocumentNumber must not be null");
            else if (documentRequestDto.getCheckNumber() == null || documentRequestDto.getCheckNumber().isEmpty())
                throw Responses.forbidden("CheckNumber must not be null");
            else if (documentRequestDto.getDocumentAmount() == null)
                throw Responses.forbidden("DocumentAmount must not be null");
            else if (documentRequestDto.getDocumentDate() == null)
                throw Responses.forbidden("DocumentDate must not be null");
            else if (documentRequestDto.getBranchId() == null)
                throw Responses.forbidden("BranchId must not be null");
        } else if (documentRequestDto.getDocumentType().equals(DocumentRequestTypeEnum.DAILY) && (documentRequestDto.getRequestType().equals(GROUP_DOCUMENT_IMAGE))) {
            if (documentRequestDto.getBranchId() == null)
                throw Responses.forbidden("BranchId must not be null");
            else if (documentRequestDto.getDocumentDateFrom() == null)
                throw Responses.forbidden("Document Date From must not be null");
            else if (documentRequestDto.getDocumentDateTo() == null)
                throw Responses.forbidden("Document Date to must not be null");
        } else if (documentRequestDto.getDocumentType().equals(DocumentRequestTypeEnum.CHAKAVAK) && (documentRequestDto.getRequestType().equals(DOCUMENT_IMAGE) || documentRequestDto.getRequestType().equals(DOCUMENT_ORIGINAL))) {
            if (documentRequestDto.getCheckNumber() == null || documentRequestDto.getCheckNumber().isEmpty())
                throw Responses.forbidden("check number must not be null");
            else if (documentRequestDto.getDocumentAmount() == null)
                throw Responses.forbidden("check amount must not be null");
            else if (documentRequestDto.getCheckDate() == null)
                throw Responses.forbidden("check date must not be null");
            else if (documentRequestDto.getBranchId() == null)
                throw Responses.forbidden("BranchId must not be null");
            else if (documentRequestDto.getCheckReceiptDate() == null)
                throw Responses.forbidden("check receipt date must not be null");
            else if (documentRequestDto.getCheckIssuingBank() == null || documentRequestDto.getCheckIssuingBank().isEmpty())
                throw Responses.forbidden("check Issuing Bank must not be null");
        } else if (documentRequestDto.getDocumentType().equals(DocumentRequestTypeEnum.CHAKAVAK) && (documentRequestDto.getRequestType().equals(GROUP_DOCUMENT_IMAGE))) {
            if (documentRequestDto.getBranchId() == null)
                throw Responses.forbidden("BranchId must not be null");
            else if (documentRequestDto.getCheckReceiptDateFrom() == null)
                throw Responses.forbidden("Check Receipt Date from must not be null");
            else if (documentRequestDto.getCheckReceiptDateTo() == null)
                throw Responses.forbidden("Check Receipt Date  to must not be null");
        } else if (documentRequestDto.getDocumentType().equals(DocumentRequestTypeEnum.OTHER_BANKING_OPERATIONS) && (documentRequestDto.getRequestType().equals(DOCUMENT_IMAGE) || documentRequestDto.getRequestType().equals(DOCUMENT_ORIGINAL))) {
            if (documentRequestDto.getCustomerNumber() == null || documentRequestDto.getCustomerNumber().isEmpty())
                throw Responses.forbidden("customer number must not be null");
            else if (documentRequestDto.getFileNumber() == null || documentRequestDto.getFileNumber().isEmpty())
                throw Responses.forbidden("file number must not be null");
            else if (documentRequestDto.getFileDate() == null)
                throw Responses.forbidden("file date must not be null");
        } else if (documentRequestDto.getDocumentType().equals(DocumentRequestTypeEnum.OTHER_BANKING_OPERATIONS) && (documentRequestDto.getRequestType().equals(GROUP_DOCUMENT_IMAGE))) {
            if (documentRequestDto.getBranchId() == null)
                throw Responses.forbidden("BranchId must not be null");
            else if (documentRequestDto.getFileDateFrom() == null)
                throw Responses.forbidden("file date from must not be null");
            else if (documentRequestDto.getFileDateTo() == null)
                throw Responses.forbidden("file date to must not be null");
        }
    }
}
