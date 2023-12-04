package com.sima.dms.repository;

import com.sima.dms.domain.entity.NodeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NodeBaseRepository extends JpaRepository<NodeBase,String> {

    NodeBase findByUuid(String uuid);

    @Query("select n.parent from NodeBase n where n.uuid = :uuid ")
    String getParent(@Param("uuid") String uuid);

    @Query("select n.name from NodeBase n where n.uuid = :uuid ")
    String getName(@Param("uuid") String uuid);

    @Transactional
    @Modifying
    void deleteByUuid(String uuid);


    @Query(value = " select folder.NBS_UUID from OKM_NODE_FOLDER folder join OKM_NODE_BASE onb on onb.NBS_UUID = folder.NBS_UUID " +
            " where onb.NBS_CONTEXT='okm_root' and onb.NBS_UUID not in ( select NBS_PARENT from OKM_NODE_BASE ) " , nativeQuery = true)
    List<String> getChildrenUuid();

    @Modifying
    @Query(value = "delete from OKM_NODE_BASE where NBS_PARENT =:parentUuid ",nativeQuery = true)
    void deleteByParentUuid(@Param("parentUuid") String parentUuid);

}
