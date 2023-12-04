package com.sima.dms.service.impl;

import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.sima.dms.constants.OpenKM;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.NodeBase;
import com.sima.dms.domain.enums.ObjectName;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.repository.*;
import com.sima.dms.service.PermissionService;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.sima.dms.domain.entity.session.Authorized.currentUserId;
import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final BranchRepository branchRepository;
    private final ProfileRepository profileRepository;
    private final DocumentRepository documentRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final DocumentSetRepository documentSetRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final OtherDocumentRepository otherDocumentRepository;

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public void checkPermission(ObjectName objectName, List<Long> objectIds) {

        final Long currentUserId = currentUserId();
        final RoleEnum role = profileRepository.getRole(currentUserId);

        switch (role) {
            case BU:
                checkBUPermissions(objectName, objectIds, currentUserId);
                break;
            case BA:
                checkBAPermissions(objectName, objectIds, currentUserId);
                break;
            case DOEU:
            case DOPU:
                checkDOUPermissions(objectName, objectIds, currentUserId);
                break;
        }
    }

    public void checkBUPermissions(ObjectName objectName, List<Long> objectIds, Long currentUserId) {
        Long userBranchId = profileRepository.getBranchId(currentUserId);
        switch (objectName) {
            case Document:
                checkList(documentRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
            case DocumentSet:
                checkList(documentSetRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
            case DocumentRequest:
                checkList(documentRequestRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
            case OtherDocument:
                checkList(otherDocumentRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
        }
    }

    public void checkBAPermissions(ObjectName objectName, List<Long> objectIds, Long currentUserId) {
        Long userBranchId = profileRepository.getBranchId(currentUserId);
        switch (objectName) {
            case Document:
                checkListBA(documentRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
            case DocumentSet:
                checkListBA(documentSetRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
            case DocumentRequest:
                checkListBA(documentRequestRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
            case OtherDocument:
                checkListBA(otherDocumentRepository.getCreatorBranchIds(objectIds), userBranchId);
                break;
        }
    }

    public void checkDOUPermissions(ObjectName objectName, List<Long> objectIds, Long currentUserId) {
        List<Long> assignedBranches = profileRepository.getAssignedBranches(currentUserId).stream().map(branch -> branch.getId()).collect(Collectors.toList());
        switch (objectName) {
            case Document:
                if (!assignedBranches.containsAll(documentRepository.getCreatorBranchIds(objectIds)))
                    throw Responses.unauthorized("you have not permission to this operation.");
                break;
            case DocumentSet:
                if (!assignedBranches.containsAll(documentSetRepository.getCreatorBranchIds(objectIds)))
                    throw Responses.unauthorized("you have not permission to this operation.");
                break;
            case DocumentRequest:
                if (!assignedBranches.containsAll(documentRequestRepository.getCreatorBranchIds(objectIds)))
                    throw Responses.unauthorized("you have not permission to this operation.");
                break;
        }
    }

    public void checkList(List<Long> list, Long userBranchId) {
        if (!isNull(list) && !list.isEmpty()) {
            if (list.size() > 1)
                throw Responses.unauthorized("you have not permission to this operation.");
            if (!(list.stream().findFirst().get().equals(userBranchId)))
                throw Responses.unauthorized("you have not permission to this operation.");
        } else
            throw Responses.unauthorized("you have not permission to this operation.");
    }

    public void checkListBA(List<Long> list, Long userBranchId) {
        if (!isNull(list) && !list.isEmpty()) {
            List<Long> branches = branchRepository.getAllByParentId(userBranchId);
            if (!branches.containsAll(list))
                throw Responses.unauthorized("you have not permission to this operation.");
        } else
            throw Responses.unauthorized("you have not permission to this operation.");
    }

    @Override
    public void setBranchPermissions() {

        List<Branch> branches = branchRepository.findAllByPathIsNotNull();
        try {

            for (Branch branch : branches) {
                String uuid = webservices.getNodeUuid(branch.getPath());
                setPermission(uuid, branch.getBranchCode());
            }

        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    private void setPermission(String uuid, Long branchCode) {
        NodeBase nodeBase = nodeBaseRepository.findByUuid(uuid);
        documentRepository.setRolePermission(uuid, 15L, branchCode.toString());
        if (!nodeBase.getName().equals("province")) {
            setPermission(nodeBase.getParent(), branchCode);
        }
    }
}
