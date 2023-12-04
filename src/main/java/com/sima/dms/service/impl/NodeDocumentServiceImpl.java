package com.sima.dms.service.impl;

import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.bean.Version;
import com.sima.dms.constants.OpenKM;
import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.request.MetadataDto;
import com.sima.dms.domain.dto.request.SetMetadataRequestDto;
import com.sima.dms.domain.entity.NodeDocument;
import com.sima.dms.domain.entity.NodeProperty;
import com.sima.dms.domain.enums.MetadataFieldNameEnum;
import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.repository.*;
import com.sima.dms.service.NodeDocumentService;
import com.sima.dms.service.ThumbnailService;
import com.sima.dms.service.mapper.NodeDocumentMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.sima.dms.utils.Responses.notFound;


/**
 * Service Implementation for managing {@link NodeDocumentService}.
 */

@Service
//@Transactional
@AllArgsConstructor
public class NodeDocumentServiceImpl implements NodeDocumentService {

    @Autowired
    Environment env;

    private static int exceptionCounter = 0;

    private final ThumbnailService thumbnailService;

    private final NodeDocumentMapper nodeDocumentMapper;

    private final ProfileRepository profileRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final NodeDocumentRepository nodeDocumentRepository;
    private final NodePropertyRepository nodePropertyRepository;
    private final NodeFolderRepository getFoldersByRolePermission;

    private final Logger log = LoggerFactory.getLogger(NodeDocumentServiceImpl.class);

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public synchronized Document createDocument(Document doc, InputStream is) throws GenericException, IOException {
        log.debug("Request to create document in : {}", doc);
        try {
            Document document = webservices.createDocument(doc, is);
            IOUtils.closeQuietly(is);
            return document;
        } catch (Exception e) {
            log.error("error in create document: " + doc);
            exceptionCounter++;
//            runCleanup();
            throw new GenericException(e);
        }
    }

    @Override
    public Version updateDocument(String uuid, InputStream is) throws GenericException, IOException {
        log.debug("Request to update document : {}", uuid, is);
        try {
            webservices.checkout(uuid);
            return webservices.checkin(uuid, is, "updateFile_" + Instant.now());
        } catch (Exception e) {
            log.error("error in update document: " + uuid);
            exceptionCounter++;
//            runCleanup();
            throw new GenericException(e);
        }
    }

    @Override
    public NodeDocumentDto findByUuid(String uuid) {
        log.debug("Request to find node document : ", uuid);
        NodeDocument nodeDocument = nodeDocumentRepository.findById(uuid)
                .orElseThrow(() -> notFound("Node Document not found"));
        return nodeDocumentMapper.toDto(nodeDocument);
    }

    @Override
    public List<NodeDocumentDto> findByParent(String parentUuid) {
        return null;
    }

    @Override
    public void deleteDocument(String uuid) {

    }

    @Override
    public void deleteFolder(String uuid) {

    }

    @Override
    public List<Folder> getUserFolders(String folderUuid) {
        return null;
    }

    @Override
    public List<Document> getUserDocuments(String folderUuid) {
        return null;
    }

//    @Override
//    public void deleteDocument(String uuid) {
//        log.debug("Request to delete document : ", uuid);
//        try {
//            webservices.deleteDocument(uuid);
//        } catch (Exception e) {
//            log.error("Error in delete document : {}");
//        }
//    }
//
//    @Override
//    public void deleteFolder(String uuid) {
//        log.debug("Request to delete document : ", uuid);
//        try {
//            webservices.deleteFolder(uuid);
//        } catch (Exception e) {
//            log.error("Error in delete document : {}");
//        }
//    }

//    @Override
//    public List<Folder> getUserFolders(String folderUuid) {
//        try {
//            List<Folder> folders = new ArrayList<>();
//
//            if (!isNull(currentUserId())) {
//                Profile profile = profileRepository.findById(currentUserId())
//                        .orElseThrow(() -> notFound("profile not found"));
//
//                List<String> folderUuids;
//                switch (profile.getRole()) {
//                    case ADMIN:
//                    case DOA:
//                        if (isNull(folderUuid)) {
//                            if (webservices.isValidFolder(OpenKM.rootPath + OpenKM.provinceFolder))
//                                folders = webservices.getFolderChildren(OpenKM.rootPath + OpenKM.provinceFolder);
//                        } else folders = webservices.getFolderChildren(folderUuid);
//                        break;
//                    case DOU:
//
//                        List<Long> branchCodes = profileRepository.getAssignedBranches(profile.getId()).stream().map(branch -> branch.getBranchCode()).collect(Collectors.toList());
//                        if (isNull(folderUuid)) {
//                            if (webservices.isValidFolder(OpenKM.rootPath + OpenKM.provinceFolder)) {
//                                Folder province = webservices.getFolderProperties(OpenKM.rootPath + OpenKM.provinceFolder);
//                                folderUuids = getFoldersByRolePermission.getFoldersByRolePermission(branchCodes, province.getUuid());
//                                for (String uuid : folderUuids) {
//                                    folders.add(webservices.getFolderProperties(uuid));
//                                }
//                            }
//                        } else {
//                            folderUuids = getFoldersByRolePermission.getFoldersByRolePermission(branchCodes, folderUuid);
//                            for (String uuid : folderUuids) {
//                                folders.add(webservices.getFolderProperties(uuid));
//                            }
//                        }
//                        break;
//
//                    case BA:
//                    case BU:
//
//                        if (isNull(folderUuid)) {
//                            if (webservices.isValidFolder(OpenKM.rootPath + OpenKM.provinceFolder)) {
//                                Folder province = webservices.getFolderProperties(OpenKM.rootPath + OpenKM.provinceFolder);
//                                folderUuids = getFoldersByRolePermission.getFoldersByRolePermission(profile.getBranch().getBranchCode().toString(), province.getUuid());
//                                for (String uuid : folderUuids) {
//                                    folders.add(webservices.getFolderProperties(uuid));
//                                }
//                            }
//                        } else {
//                            folderUuids = getFoldersByRolePermission.getFoldersByRolePermission(profile.getBranch().getBranchCode().toString(), folderUuid);
//                            for (String uuid : folderUuids) {
//                                folders.add(webservices.getFolderProperties(uuid));
//                            }
//                        }
//                        break;
//                }
//            }
//            return folders;
//        } catch (Exception e) {
//            throw new GenericException(e);
//        }
//    }
//
//    @Override
//    public List<Document> getUserDocuments(String folderUuid) {
//        try {
//            List<Document> documents = new ArrayList<>();
//            if (!isNull(currentUserId())) {
//                Profile profile = profileRepository.findById(currentUserId())
//                        .orElseThrow(() -> notFound("profile not found"));
//
//                List<String> documentUuids;
//
//                switch (profile.getRole()) {
//                    case ADMIN:
//                    case DOA:
//                        documents = webservices.getDocumentChildren(folderUuid);
//                        break;
//                    case DOU:
//                        List<Long> branchCodes = profileRepository.getAssignedBranches(profile.getId()).stream().map(branch -> branch.getBranchCode()).collect(Collectors.toList());
//                        documentUuids = nodeDocumentRepository.getDocumentByRolePermission(branchCodes, folderUuid);
//                        for (String uuid : documentUuids) {
//                            documents.add(webservices.getDocumentProperties(uuid));
//                        }
//                        break;
//                    case BA:
//                    case BU:
//                        documentUuids = nodeDocumentRepository.getDocumentByRolePermission(profile.getBranch().getBranchCode().toString(), folderUuid);
//                        for (String uuid : documentUuids) {
//                            documents.add(webservices.getDocumentProperties(uuid));
//                        }
//                        break;
//                }
//            }
//            return documents;
//        } catch (Exception e) {
//            throw new GenericException(e);
//        }
//    }

//    @Override
//    public Pair<String, ByteArrayOutputStream> getByWatermark(String docId) {
//        log.debug("Request to get content by watermark : {}", docId);
//        try {
//            String fullName = profileRepository.getFullName(currentUser().getId());
//            StringBuilder userData = new StringBuilder();
//            userData.append(fullName)
//                    .append(" at ")
//                    .append(Instant.now().toString());
//            InputStream is = webservices.getContent(docId);
//            Document document = webservices.getDocumentProperties(docId);
//            String mimeType = document.getMimeType();
//            String path = document.getPath();
//            String[] split = path.split("/");
//            String fileName = split[split.length - 1];
//            ByteArrayOutputStream outputStream;
//            if (mimeType.contains("pdf")) {
//                outputStream = WatermarkUtils.addWatermark(is, userData.toString());
//            } else if (mimeType.contains("image")) {
//                outputStream = WatermarkUtils.addTextWatermarkOnImage(is, userData.toString());
//            } else {
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                IOUtils.copy(is, byteArrayOutputStream);
//                byteArrayOutputStream.close();
//                outputStream = byteArrayOutputStream;
//            }
//            IOUtils.closeQuietly(is);
//            return Pair.of(fileName, outputStream);
//        } catch (Exception e) {
//            throw new GenericException(e);
//        }
//    }
//
//    @Override
//    public List<NodeDocumentDto> findByParent(String parentUuid) {
//        log.debug("Request to find all by parent uuid: {}", parentUuid);
//        List<NodeDocument> nodeDocuments = nodeDocumentRepository.findAllByParent(parentUuid);
//        List<NodeDocumentDto> nodeDocumentsDto = new ArrayList<>();
//        nodeDocuments.forEach(nodeDocument -> {
//            NodeDocumentDto nodeDocumentDto = nodeDocumentMapper.toDto(nodeDocument);
//
//            try {
//                if (nodeDocument.getMimeType().contains("image") || nodeDocument.getMimeType().contains("pdf")) {
//
//                    if (nodeDocument.getThumbnails() != null && !nodeDocument.getThumbnails().isEmpty()) {
//                        nodeDocumentDto.setThumbnailLink("http://okmAdmin:admin@localhost:4200/OpenKM/Download?uuid=" + nodeDocument.getThumbnails().stream().findFirst().get());
//                    } else {
//                        Document thumbnail = thumbnailService.createThumbnail(nodeDocument.getUuid(), webservices.getContent(nodeDocument.getUuid()), nodeDocument.getMimeType());
//                        nodeDocumentDto.setThumbnailLink("http://okmAdmin:admin@localhost:4200/OpenKM/Download?uuid=" + thumbnail.getUuid());
//                    }
//                }
//            } catch (Exception e) {
//                log.debug("error in get thumbnail");
//            }
//            nodeDocumentsDto.add(nodeDocumentDto);
//        });
//        return nodeDocumentsDto;
//    }

    @Override
    public void setMetadata(SetMetadataRequestDto metadata) {
        nodeDocumentRepository.deleteMetadataByUuid(metadata.getUuid());
        metadata.getMetadata().forEach(metadataDto -> {
            nodeDocumentRepository.insertMetadata(metadataDto.getName().getGroup(), metadataDto.getName().getValue(), metadataDto.getValue(), metadata.getUuid());
        });

    }

    @Override
    public List<MetadataDto> getDocumentMetadata(String uuid) {

        log.debug("Request to get document metadata : ", uuid);

        List<MetadataDto> metadata = new ArrayList<>();

        List<NodeProperty> nodeProperties = nodePropertyRepository.findAllByNode_UuidAndValueIsNot(uuid, "");

        if (nodeProperties != null && !nodeProperties.isEmpty())
            nodeProperties.forEach(nodeProperty -> {
                MetadataFieldNameEnum fieldName = MetadataFieldNameEnum.valueOf(nodeProperty.getName());
                MetadataDto metadataDto = new MetadataDto(fieldName, nodeProperty.getValue());
                metadata.add(metadataDto);
            });

        return metadata;
    }

    @Override
    public InputStream getContent(String uuid) throws IOException {
        try {
            return webservices.getContent(uuid);
        } catch (Exception e) {
            log.error("error in getContent document: " + uuid);
            exceptionCounter++;
            return null;
//            throw new GenericException(e);
        }
    }

    @Override
    public void moveDocument(String uuid, String path) {
        try {
            webservices.moveDocument(uuid, path);
        } catch (Exception e) {
            log.debug("error in move document");
        }
    }

    @Override
    public String getName(String uuid) {
        return nodeBaseRepository.getName(uuid);
    }

    @Override
    public void addCategory(String id, String name) {
        try {
            webservices.addCategory(id, name);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public void deleteCategory(String id, String name) {
        try {
            webservices.removeCategory(id, name);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public List<Document> getDocumentChildren(String var1) {
        try {
            return webservices.getDocumentChildren(var1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void renameDocument(String name, String name2) {

        try {
            webservices.renameDocument(name, name2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPath(String path) {
        try {
            return webservices.getDocumentPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getContentByVersion(String var1, String var2) {
        try {
            return webservices.getContentByVersion(var1, var2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Version> getVersionHistory(String var1) {
        try {
            return webservices.getVersionHistory(var1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restoreVersion(String var1, String var2) {
        try {
            webservices.restoreVersion(var1, var2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void purgeVersionHistory(String var1) {
        try {
            webservices.purgeVersionHistory(var1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Version checkIn(String var1, InputStream var2, String var3) {
        try {
            return webservices.checkin(var1, var2, var3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkOut(String uuid) {
        try {
            webservices.checkout(uuid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extendedCopy(String var1, String var2, String var3, boolean a, boolean b, boolean c, boolean d, boolean e) {
        try {
            webservices.extendedDocumentCopy(var1, var2, var3, a, b, c, d, e);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Document createSimple(String var1, InputStream var2) {
        try {
            return webservices.createDocumentSimple(var1, var2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNodeUuid(String var1) {
        try {
            return webservices.getNodeUuid(var1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Document getProperties(String docId) {
        try {
            return webservices.getDocumentProperties(docId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMimeType(String uuid) {
        return nodeDocumentRepository.getMimeType(uuid);
    }

    @Override
    public synchronized void cleanup() throws IOException {

        log.info("start running cleanup.bat");
        Runtime.getRuntime().exec("cmd /c start " + env.getProperty("cleanup.bat.directory"));
        exceptionCounter = 0;
        log.info("cleanup work finished");

//        if (exceptionCounter > 2) {
//            log.info("start running cleanup.bat");
//            Runtime.getRuntime().exec("cmd /c start " + env.getProperty("cleanup.bat.directory"));
//            exceptionCounter = 0;
//            log.info("cleanup work finished");
//        }

    }

}
