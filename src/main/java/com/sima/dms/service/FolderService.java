package com.sima.dms.service;

import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;

import java.util.List;

public interface FolderService {

    Folder createFolder(String path);

    Folder createFolderAndSetPermission(String path,String branchCode);

    Folder createFolderWithPath(String path);

//    Boolean checkFolderExist(String path);

    Boolean isValidFolder(String path);

    void renameFolder(String uuid, String newName);

    void deleteFolder(String uuid);

    List<String> getFolderPath(List<String> uuids);

    String getFolderPath(String uuid);

    List<Document> getFolderDocuments(String uuids);

    List<Folder> getFolderChildren(String folder);

    void folderCopy(String var1,String var2);

    void rename(String var1,String var2);

    void folderMove(String var1,String var2);
    void folderDelete(String var1);
    Folder createFolderSimple(String path);


}
