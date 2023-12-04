package com.sima.dms.service.impl;

import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.bean.Node;
import com.sima.dms.constants.OpenKM;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.repository.BranchRepository;
import com.sima.dms.repository.NodeBaseRepository;
import com.sima.dms.repository.NodeFolderRepository;
import com.sima.dms.repository.ProfileRepository;
import com.sima.dms.service.FolderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.enums.RoleEnum.*;
import static com.sima.dms.utils.Responses.notFound;
import static java.util.Objects.isNull;

/**
 * Service Implementation for managing {@link FolderService}.
 */

@Service
//@Transactional
@AllArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final ProfileRepository profileRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final BranchRepository branchRepository;
    private final NodeFolderRepository nodeFolderRepository;
    private final Logger log = LoggerFactory.getLogger(FolderServiceImpl.class);
    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public Folder createFolder(String path) throws GenericException {
        try {
            return webservices.createFolderSimple(OpenKM.rootPath + path);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public Folder createFolderAndSetPermission(String path, String branchCode) throws GenericException {
        try {
            Folder folder = webservices.createFolderSimple(OpenKM.rootPath + path);
            //documentRepository.setRolePermission(folder.getUuid(),15L,branchCode);
            return folder;
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public Folder createFolderWithPath(String path) throws GenericException {
        try {
            Folder folder = webservices.createFolderSimple(path);
            //documentRepository.setRolePermission(folder.getUuid(),15L,branchCode);
            return folder;
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public Boolean isValidFolder(String path) {
        try {
            return webservices.isValidFolder(path);
        } catch (Exception e) {
            log.debug("Intended folder not exist");
            return false;
        }
    }

    @Override
    public void renameFolder(String uuid, String newName) {
        try {
            webservices.renameFolder(uuid, newName);
        } catch (Exception e) {
            log.debug("error in rename folder");
        }
    }

    @Override
    public void deleteFolder(String uuid) {
        try {
            webservices.deleteFolder(uuid);
        } catch (Exception e) {
            log.debug("error in deleting folder");
        }
    }

    @Override
    public List<String> getFolderPath(List<String> uuids) {
        List<String> folderPaths = new ArrayList<>();
        uuids.forEach(uuid -> {
            try {
                String path = webservices.getFolderPath(uuid);
                folderPaths.add(path);
            } catch (Exception e) {
                log.debug("error in get Folder Path");
            }
        });
        return folderPaths;
    }

    @Override
    public String getFolderPath(String uuid) {
        try {
            return webservices.getFolderPath(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Document> getFolderDocuments(String uuids) {
        try {

            return webservices.getDocumentChildren(uuids);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Folder> getFolderChildren(String uuid) {

        List<Folder> folders = new ArrayList<>();
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        try {
            if (isNull(uuid)) {
                if (profile.getRole().equals(ADMIN)) {
                    if (webservices.isValidFolder(OpenKM.rootPath + OpenKM.provinceFolder))
                        folders.add(webservices.getFolderProperties(OpenKM.rootPath + OpenKM.provinceFolder));
                    if (webservices.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolder))
                        folders.add(webservices.getFolderProperties(OpenKM.rootPath + OpenKM.otherDocumentFolder));
                } else if (profile.getRole().equals(BU) || profile.getRole().equals(BA)) {
                    if (webservices.isValidFolder(OpenKM.rootPath + OpenKM.otherDocumentFolder))
                        folders.add(webservices.getFolderProperties(OpenKM.rootPath + OpenKM.otherDocumentFolder));
                }
            } else {
                if (profile.getRole().equals(ADMIN)) {
                    folders = webservices.getFolderChildren(uuid);
                } else if (profile.getRole().equals(BU)) {

                    setFolders(uuid, profile.getBranch().getBranchCode(), folders);
                } else if (profile.getRole().equals(BA)) {
                    List<Long> branches = branchRepository.getAllBranchCodeByParentId(profile.getBranch().getId());
                    for (Long code : branches) {
                        setFolders(uuid, code, folders);
                    }
                }
            }
            return folders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void folderCopy(String var1, String var2) {
        try {
            webservices.copyFolder(var1, var2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rename(String var1, String var2) {
        try {
            webservices.renameFolder(var1, var2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void folderMove(String var1, String var2) {
        try {
            webservices.moveFolder(var1, var2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void folderDelete(String var1) {
        try {
            webservices.deleteFolder(var1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Folder createFolderSimple(String path) {
        try {
            return webservices.createFolderSimple(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getParents(String uuid, List<String> parents) {
        parents.add(uuid);
        String parent = nodeBaseRepository.getParent(uuid);
        if (parent != null) {
            getParents(parent, parents);
        }
        return parents;
    }

    private void setFolders(String uuid, Long code, List<Folder> folders) {
        try {
            List<String> folderUuids = nodeFolderRepository.getFoldersByBranchIds(code);
            for (String folderUuid : folderUuids) {
                if (webservices.getFolderPath(folderUuid).contains("otherDocument")) {
                    List<String> parents = getParents(folderUuid, new ArrayList<>());
                    if(folderUuid.equals(uuid) || !parents.contains(uuid)){
                        folders.addAll(webservices.getFolderChildren(uuid));
                    }
                    else{
                        List<String> children = webservices.getFolderChildren(uuid).stream().map(Node::getUuid).collect(Collectors.toList());
                        children.retainAll(parents);
                        for (String child : children) {
                            folders.add(webservices.getFolderProperties(child));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
