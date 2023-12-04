package com.sima.dms.service;

import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.bean.Version;
import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.request.MetadataDto;
import com.sima.dms.domain.dto.request.SetMetadataRequestDto;
import com.sima.dms.errors.exceptions.GenericException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface NodeDocumentService {

    Document createDocument(Document document, InputStream is) throws GenericException, IOException;

    Version updateDocument(String uuid, InputStream is) throws GenericException, IOException;

    NodeDocumentDto findByUuid(String uuid);

    List<NodeDocumentDto> findByParent(String parentUuid);

    void deleteDocument(String uuid);

    void deleteFolder(String uuid);

    List<Folder> getUserFolders(String folderUuid);

    List<Document> getUserDocuments(String folderUuid);

    void setMetadata(SetMetadataRequestDto metadata);

    List<MetadataDto> getDocumentMetadata(String uuid);

    void moveDocument(String uuid, String path);

    String getName(String uuid);


    void addCategory(String id, String name);

    void deleteCategory(String id, String name);

    List<Document> getDocumentChildren(String var1);

    void renameDocument(String name, String name2);

    String getPath(String path);

    InputStream getContent(String uuid) throws IOException;

    InputStream getContentByVersion(String var1, String var2);

    List<Version> getVersionHistory(String var1);

    void restoreVersion(String var1, String var2);

    void purgeVersionHistory(String var1);

    void checkOut(String var1);

    Version checkIn(String var1, InputStream var2, String var3);

    void extendedCopy(String var1, String var2, String var3, boolean a, boolean b, boolean c, boolean d, boolean e);

    String getNodeUuid(String var1);

    Document createSimple(String var1, InputStream var2);

    Document getProperties(String docId);

    String getMimeType(String uuid);

    void cleanup() throws IOException;

//    Pair<String, ByteArrayOutputStream> getByWatermark(String docId);
}
