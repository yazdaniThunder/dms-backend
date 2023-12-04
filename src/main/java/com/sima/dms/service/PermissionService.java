package com.sima.dms.service;

import com.sima.dms.domain.enums.ObjectName;

import java.util.List;

public interface PermissionService {

    void checkPermission(ObjectName objectName, List<Long> objectIds);

    void setBranchPermissions();
}
