package com.sima.dms.repository;

import com.openkm.sdk4j.bean.Folder;
import com.sima.dms.domain.entity.NodeFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodeFolderRepository extends JpaRepository<NodeFolder, String> {

    @Query(value = "select nf.NBS_UUID from OKM_NODE_FOLDER nf" +
            " join OKM_NODE_ROLE_PERMISSION nrp on nrp.NRP_NODE = nf.NBS_UUID" +
            " join OKM_NODE_BASE onb on onb.NBS_UUID = nf.NBS_UUID" +
            " where nrp.NRP_ROLE =:role and onb.NBS_PARENT =:parent", nativeQuery = true)
    List<String> getFoldersByRolePermission(@Param("role") String role, @Param("parent") String parent);

    @Query(value = "select nf.NBS_UUID from OKM_NODE_FOLDER nf" +
            " join OKM_NODE_ROLE_PERMISSION nrp on nrp.NRP_NODE = nf.NBS_UUID" +
            " join OKM_NODE_BASE onb on onb.NBS_UUID = nf.NBS_UUID" +
            " where nrp.NRP_ROLE in :roles and onb.NBS_PARENT =:parent", nativeQuery = true)
    List<String> getFoldersByRolePermission(@Param("roles") List<String> roles, @Param("parent") String parent);

    @Query(value = "select onf.NBS_UUID from okm_node_base onb " +
            " inner join okm_node_folder onf on onb.NBS_UUID = onf.NBS_UUID " +
            " where onb.NBS_CONTEXT='okm_root' and onb.NBS_NAME like concat('%-',:branchCode,'%') " ,nativeQuery = true)
    List<String> getFoldersByBranchIds(Long branchCode);
}
