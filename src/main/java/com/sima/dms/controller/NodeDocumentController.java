package com.sima.dms.controller;

import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.bean.Version;
import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.request.MetadataDto;
import com.sima.dms.domain.dto.request.SetMetadataRequestDto;
import com.sima.dms.domain.enums.MetadataFieldNameEnum;
import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.NodeDocumentService;
import com.sima.dms.service.ThumbnailService;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.sima.dms.tools.TifToPdf.tifToPdf;
import static com.sima.dms.utils.Responses.noContent;

@Controller
@AllArgsConstructor
@Tag(name = "NodeDocuments")
@RequestMapping("/dms/nodeDocuments")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class NodeDocumentController {

    private final ThumbnailService thumbnailService;
    private final FolderService folderService;
    private final NodeDocumentService nodeDocumentService;
    private final Logger log = LoggerFactory.getLogger(NodeDocumentController.class);

    @CrossOrigin
    @PostMapping("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Create document")
    @SecurityRequirement(name = "token")
    public Document createDocument(@FormDataParam("docPath") String docPath, @FormDataParam("content") MultipartFile content) throws GenericException {
        try {
            log.debug("createSimple({})", content);

            Document doc = new Document();
            doc.setPath(docPath + "/" + content.getOriginalFilename());
            doc.setMimeType(content.getContentType());
            Document newDocument = nodeDocumentService.createDocument(doc, content.getInputStream());

            if (content.getContentType().contains("image") || content.getContentType().contains("pdf")) {
                thumbnailService.createThumbnail(newDocument.getUuid(), content);
            }

            IOUtils.closeQuietly(content.getInputStream());
            return newDocument;
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @CrossOrigin
    @GetMapping("/{uuid}")
    @Operation(summary = "Get node document")
    @SecurityRequirement(name = "token")
    public ResponseEntity<NodeDocumentDto> getNodeDocument(@PathVariable String uuid) {
        log.debug("get node document", uuid);
        NodeDocumentDto nodeDocumentDto = nodeDocumentService.findByUuid(uuid);
        return ResponseEntity.ok().body(nodeDocumentDto);
    }

//    @CrossOrigin
//    @GetMapping("/folders")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Get folder children")
//    public ResponseEntity<List<Folder>> getUserFolders(@RequestParam(required = false) String folderUuid) {
//        return ok(nodeDocumentService.getUserFolders(folderUuid));
//    }
//
//    @CrossOrigin
//    @GetMapping("/documents")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Get document children")
//    public ResponseEntity<List<Document>> getUserDocuments(@RequestParam(required = false) String folderUuid) {
//        return ok(nodeDocumentService.getUserDocuments(folderUuid));
//    }

    @CrossOrigin
    @PostMapping("/setMetadata")
    @Operation(summary = "Set metadata")
    @SecurityRequirement(name = "token")
    public ResponseEntity<Void> setMetadata(@RequestBody SetMetadataRequestDto request) {
        log.debug("set metadata", request);
        nodeDocumentService.setMetadata(request);
        return noContent();
    }

    @CrossOrigin
    @GetMapping("/metadataFields")
    @Operation(summary = "Get all metadata")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<MetadataFieldNameEnum>> getAllMetadata() {
        log.debug("get all metadata");
        List<MetadataFieldNameEnum> metadata = new ArrayList<>(EnumSet.allOf(MetadataFieldNameEnum.class));
        return ResponseEntity.ok().body(metadata);
    }

    @CrossOrigin
    @GetMapping("/metadata/{uuid}")
    @Operation(summary = "Get document metadata")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<MetadataDto>> getDocumentMetadata(@PathVariable String uuid) {
        log.debug("get document metadata", uuid);
        List<MetadataDto> documentMetadata = nodeDocumentService.getDocumentMetadata(uuid);
        return ResponseEntity.ok().body(documentMetadata);
    }

    @CrossOrigin
    @GetMapping(value = "/getContent/{uuid}")
    @Operation(summary = "Get content")
    @SecurityRequirement(name = "token")
    public HttpServletResponse getContent(@PathVariable String uuid, @RequestParam(required = false,defaultValue = "false") boolean isDownload, HttpServletResponse response) throws Exception {

        log.debug("REST request to get content by docId : {}", uuid);

        InputStream result = nodeDocumentService.getContent(uuid);
        NodeDocumentDto documentDto = nodeDocumentService.findByUuid(uuid);

        response.setHeader("Content-disposition", "attachment:filename=" + documentDto.getName());

        if(isDownload){
            response.setHeader("Content-disposition", "attachment:filename=" + documentDto.getName());
        }else {
            if (documentDto.getMimeType().equals("image/tif") || documentDto.getMimeType().equals("image/tiff")) {

                result = new ByteArrayInputStream(tifToPdf(result).toByteArray());
                String name = documentDto.getName();
                name = name.replace(".tiff", "");
                name = name.replace(".tif", "");
                response.setHeader("Content-disposition", "attachment:filename=" + name + ".pdf");

            } else
                response.setHeader("Content-disposition", "attachment:filename=" + documentDto.getName());
        }
        response.setHeader("Access-Control-Expose-Headers", "*");
        response.getOutputStream().write(IOUtils.toByteArray(result));
        response.getOutputStream().flush();
        response.getOutputStream().close();
        return response;
    }

    @CrossOrigin
    @PostMapping
    @Operation(summary = "Create category")
    @SecurityRequirement(name = "token")
    public void create(@RequestBody String id, @RequestBody String name) {
        log.debug("REST request to create", id);
        nodeDocumentService.addCategory(id, name);
    }

    @CrossOrigin
    @DeleteMapping
    @Operation(summary = "Delete category")
    @SecurityRequirement(name = "token")
    public void delete(@PathVariable String id, @PathVariable String name) {
        log.debug("REST request to delete", id);
        nodeDocumentService.deleteCategory(id, name);
    }

    @CrossOrigin
    @PostMapping(value = "/checkin")
    @Operation(summary = "checkin")
    @SecurityRequirement(name = "token")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<Version> checkin(@FormDataParam("docId") String docId, @FormDataParam("content") MultipartFile content, @FormDataParam("comment") String comment) throws IOException {
        log.debug("REST request to checkin", docId);
        Version version = nodeDocumentService.checkIn(docId, content.getInputStream(), comment);
        IOUtils.closeQuietly(content.getInputStream());
        return ResponseEntity.ok().body(version);
    }

    @CrossOrigin
    @PostMapping("/createSimple")
    @Operation(summary = "create Simple")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = "token")
    public ResponseEntity<Document> createSimple(@FormDataParam("docPath") String docPath, @FormDataParam("content") MultipartFile content) {
        log.debug("REST request to create Simple", docPath);
        try {
            Document document = nodeDocumentService.createSimple(docPath, content.getInputStream());
            IOUtils.closeQuietly(content.getInputStream());
            return ResponseEntity.ok().body(document);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @CrossOrigin
    @PutMapping("/move")
    @Operation(summary = "move document")
    @SecurityRequirement(name = "token")
    public void moveDocument(@RequestParam String docId, @RequestParam String dstId) {
        log.debug("REST request to move document");
        nodeDocumentService.moveDocument(docId, dstId);
    }


    @CrossOrigin
    @GetMapping("/documents")
    @Operation(summary = "get Document Children")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<Document>> getDocuments(@RequestParam(required = false) String folderUuid) {
        log.debug("REST request to get document", folderUuid);
        List<Document> document = nodeDocumentService.getDocumentChildren(folderUuid);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @GetMapping("/folders")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @Operation(summary = "get folder children")
    public ResponseEntity<List<Folder>> getFolders(@RequestParam(required = false) String folderUuid) {
        log.debug("REST request to get folders", folderUuid);
        List<Folder> folders = folderService.getFolderChildren(folderUuid);
        return ResponseEntity.ok().body(folders);
    }

    @CrossOrigin
    @DeleteMapping("/delete")
    @Operation(summary = "get Document Children")
    @SecurityRequirement(name = "token")
    public void deleteDocument(@RequestParam String docId) {
        log.debug("REST request to delete document", docId);
        nodeDocumentService.deleteDocument(docId);
    }

    @CrossOrigin
    @PutMapping("/rename")
    @Operation(summary = "move document")
    @SecurityRequirement(name = "token")
    public void rename(@RequestParam String docId, @RequestParam String newName) {
        log.debug("REST request to rename document", docId);
        nodeDocumentService.renameDocument(docId, newName);
    }

    @CrossOrigin
    @GetMapping("/getPath/{uuId}")
    @Operation(summary = "get Path")
    @SecurityRequirement(name = "token")
    public ResponseEntity<String> getDocumentPath(@PathVariable String uuId) {
        log.debug("REST request to get document path", uuId);
        if (uuId.equals("null"))
            return null;
        String document = nodeDocumentService.getPath(uuId);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @GetMapping("/checkout")
    @Operation(summary = "checkout")
    @SecurityRequirement(name = "token")
    public void checkout(@RequestParam String docId) {
        log.debug("REST request to checkout", docId);
        nodeDocumentService.checkOut(docId);
    }

    @CrossOrigin
    @GetMapping("/getContentByVersion")
    @Operation(summary = "get Content By Version")
    @SecurityRequirement(name = "token")
    public HttpServletResponse getContentByVersion(@RequestParam String docId, @RequestParam String versionId, HttpServletResponse response) throws IOException {
        log.debug("REST request to get content by version", docId);
        InputStream result = nodeDocumentService.getContentByVersion(docId, versionId);
        response.setHeader("Content-disposition", "attachment:filename=");
        response.setHeader("Access-Control-Expose-Headers", "*");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(result, byteArrayOutputStream);
        response.getOutputStream().write(byteArrayOutputStream.toByteArray());
        response.getOutputStream().flush();
        response.getOutputStream().close();
        byteArrayOutputStream.close();
        return response;


    }

    @CrossOrigin
    @GetMapping("/getVersionHistory")
    @Operation(summary = "get Version History")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<Version>> getVersionHistory(@RequestParam String docId) {
        log.debug("REST request to get version history", docId);
        List<Version> versionList = nodeDocumentService.getVersionHistory(docId);
        return ResponseEntity.ok().body(versionList);
    }

    @CrossOrigin
    @PutMapping("/restoreVersion")
    @Operation(summary = "restore Version")
    @SecurityRequirement(name = "token")
    public void restoreVersion(@RequestParam(required = false) String docId, @RequestParam(required = false) String versionId) {
        log.debug("REST request to get restore version", docId);
        nodeDocumentService.restoreVersion(docId, versionId);
    }


    @CrossOrigin
    @PutMapping("/purgeVersionHistory")
    @Operation(summary = "purge Version History")
    @SecurityRequirement(name = "token")
    public void purgeVersionHistory(@RequestParam(required = false) String docId) {
        log.debug("REST request to purge Version History", docId);
        nodeDocumentService.purgeVersionHistory(docId);
    }

    @CrossOrigin
    @PutMapping("/extendedCopy")
    @Operation(summary = "extended Copy ")
    @SecurityRequirement(name = "token")
    public void extendedCopy(@RequestParam(required = false) String docId, @RequestParam(required = false) String dstId, @RequestParam(required = false) String name,
                             @RequestParam(required = false) boolean categories,
                             @RequestParam(required = false) boolean keywords, @RequestParam(required = false) boolean propertyGroups
            , @RequestParam(required = false) boolean notes, @RequestParam(required = false) boolean wiki) {
        log.debug("REST request to extended Copy", docId);
        nodeDocumentService.extendedCopy(docId, dstId, name, categories, keywords, propertyGroups, notes, wiki);
    }

    @CrossOrigin
    @GetMapping("/documentUuid")
    @Operation(summary = "get documentUuid")
    @SecurityRequirement(name = "token")
    public ResponseEntity<String> getNodeUuid(@RequestParam String uuid) {
        log.debug("REST request to get Node Uuid", uuid);
        String documentUuid = nodeDocumentService.getNodeUuid(uuid);
        return ResponseEntity.ok().body(documentUuid);
    }

    @CrossOrigin
    @GetMapping("/getProperties")
    @Operation(summary = "get Properties")
    @SecurityRequirement(name = "token")
    public ResponseEntity<Document> getProperties(@RequestParam String docId) {
        log.debug("REST request to get Properties", docId);
        Document document = nodeDocumentService.getProperties(docId);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @PostMapping("/cleanup")
    public ResponseEntity<Void> cleanup() throws IOException {
        log.debug("REST clean OpenKM cache and index");
        nodeDocumentService.cleanup();
        return noContent();
    }


//    @CrossOrigin
//    @GetMapping("/getByWatermark/{docId}")
//    @Operation(summary = "Get by watermark")
//    public HttpServletResponse getByWatermark(@PathVariable String docId, HttpServletResponse response) throws IOException {
//        log.debug("REST request to get content by watermark for document by docId : {}", docId);
//        Pair<String, ByteArrayOutputStream> result = nodeDocumentService.getByWatermark(docId);
//        response.setHeader("Content-disposition", "attachment:filename=" + result.getFirst());
//        response.getOutputStream().write(result.getSecond().toByteArray());
//        response.getOutputStream().flush();
//        response.getOutputStream().close();
//        return response;
//    }

}
